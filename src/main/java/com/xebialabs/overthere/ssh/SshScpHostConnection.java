package com.xebialabs.overthere.ssh;

import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.HostFile;
import com.xebialabs.overthere.RuntimeIOException;

/**
 * A connection to a remote host using SSH w/ SCP.
 */
public class SshScpHostConnection extends SshHostConnection {

	public SshScpHostConnection(String type, ConnectionOptions options) {
		super(type, options);
		open();
	}

	@Override
	protected HostFile getFile(String hostPath, boolean isTempFile) throws RuntimeIOException {
		return new SshScpHostFile(this, hostPath);
	}

	public String toString() {
		return username + "@" + host + ":" + port + " (scp)";
	}

}
