package net.paissad.tools.reqcoco.maven.plugin.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Locale;

public class PathUtils {

	private PathUtils() {
	}

	public static void removeWritePerms(final Path path) throws IOException {
		if (isWindows()) {
			// Set permissions using DOS

			final UserPrincipal currentUser = path.getFileSystem().getUserPrincipalLookupService()
			        .lookupPrincipalByName(System.getProperty("user.name"));

			// get view
			final AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class);

			// create ACL to set read access for the current user
			final AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.DENY).setPrincipal(currentUser)
			        .setPermissions(AclEntryPermission.WRITE_DATA, AclEntryPermission.WRITE_ATTRIBUTES).build();

			// read ACL, insert ACL, re-write ACL
			final List<AclEntry> acl = view.getAcl();
			acl.add(0, entry); // insert before any DENY entries
			view.setAcl(acl);

		} else {
			// Set permissions using POSIX
			Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("r-xr-xr-x"));
		}
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows");
	}

}
