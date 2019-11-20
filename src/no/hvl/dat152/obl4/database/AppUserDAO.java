package no.hvl.dat152.obl4.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;

import no.hvl.dat152.obl4.util.PassordUtil;

public class AppUserDAO {

	public AppUser getAuthenticatedUser(String username, String password) {

		String query = "SELECT * FROM SecOblig.AppUser WHERE username=?";
		AppUser user = null;

		Connection c = null;
		PreparedStatement pstmt = null;
		ResultSet r = null;

		try {
			c = DatabaseHelper.getConnection();
			pstmt = c.prepareStatement(query);
			pstmt.setString(1, username);
			r = pstmt.executeQuery();

			if (r.next()) {
				if (PassordUtil.sjekkPassord(password, r.getString("passhash"))) {
					user = new AppUser(r.getString("username"), r.getString("passhash"), r.getString("firstname"),
							r.getString("lastname"), r.getString("mobilephone"), r.getString("role"));
				}

			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			DatabaseHelper.closeConnection(r, pstmt, c);
		}

		return user;
	}

	public boolean saveUser(AppUser user) {

		String query = "INSERT INTO SecOblig.AppUser VALUES (?, ?, ?, ?, ?, ?)";

		Connection c = null;
		PreparedStatement pstmt = null;
		ResultSet r = null;

		try {
			c = DatabaseHelper.getConnection();
			pstmt = c.prepareStatement(query);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPasshash());
			pstmt.setString(3, user.getFirstname());
			pstmt.setString(4, user.getLastname());
			pstmt.setString(5, user.getMobilephone());
			pstmt.setString(6, user.getRole());
			int row = pstmt.executeUpdate();
			if (row >= 0) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		} finally {
			DatabaseHelper.closeConnection(r, pstmt, c);
		}

		return false;
	}

	public List<String> getUsernames() {

		List<String> usernames = new ArrayList<String>();

		String sql = "SELECT username FROM SecOblig.AppUser";

		Connection c = null;
		Statement s = null;
		ResultSet r = null;

		try {
			c = DatabaseHelper.getConnection();
			s = c.createStatement();
			r = s.executeQuery(sql);

			while (r.next()) {
				usernames.add(r.getString("username"));
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			DatabaseHelper.closeConnection(r, s, c);
		}

		return usernames;
	}

	public boolean updateUserPassword(String username, String passwordnew) {

		String hashedPassword = generatePassHash(passwordnew);

		String query = "UPDATE SecOblig.AppUser SET passhash =? WHERE username =?";

		Connection c = null;
		PreparedStatement pstmt = null;
		ResultSet r = null;

		try {
			c = DatabaseHelper.getConnection();
			pstmt = c.prepareStatement(query);
			pstmt.setString(1, hashedPassword);
			pstmt.setString(2, username);
			int row = pstmt.executeUpdate();
			if (row >= 0) {
				System.out.println("Password update successful for " + username);
				return true;
			}

		} catch (Exception e) {
			System.out.println(e);
			return false;
		} finally {
			DatabaseHelper.closeConnection(r, pstmt, c);
		}

		return false;
	}

	public boolean updateUserRole(String username, String role) {

		String query = "UPDATE SecOblig.AppUser SET role =? WHERE username =?";

		Connection c = null;
		PreparedStatement pstmt = null;
		ResultSet r = null;

		try {
			c = DatabaseHelper.getConnection();
			pstmt = c.prepareStatement(query);
			pstmt.setString(1, role);
			pstmt.setString(2, username);
			int row = pstmt.executeUpdate();
			if (row >= 0) {
				System.out.println("Role update successful for " + username + " New role = " + role);
				return true;
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		} finally {
			DatabaseHelper.closeConnection(r, pstmt, c);
		}
		return false;
	}

	public String generatePassHash(String password) {
		return PassordUtil.krypterPassord(password);
	}

	public String generateAntiCSRFToken() {
		SecureRandom randomToken = new SecureRandom();
		byte bytetab[] = new byte[20];
		randomToken.nextBytes(bytetab);
		return DigestUtils.md5Hex(bytetab);
	}
}
