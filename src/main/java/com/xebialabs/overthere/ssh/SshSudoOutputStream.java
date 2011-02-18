package com.xebialabs.overthere.ssh;

import java.io.IOException;
import java.io.OutputStream;

import com.xebialabs.overthere.RuntimeIOException;
import org.slf4j.Logger;

import com.xebialabs.overthere.CapturingCommandExecutionCallbackHandler;
import com.xebialabs.overthere.HostFile;
import org.slf4j.LoggerFactory;

/**
 * An output stream to a file on a host connected through SSH w/ SUDO.
 */
class SshSudoOutputStream extends OutputStream {

	private HostFile tempFile;

	private SshSudoHostFile hostFile;

	private long length;

	private OutputStream tempFileOutputStream;

	public SshSudoOutputStream(SshSudoHostFile hostFile, long length, HostFile tempFile) {
		this.hostFile = hostFile;
		this.length = length;
		this.tempFile = tempFile;
	}

	void open() {
		tempFileOutputStream = tempFile.put(length);
	}

	@Override
	public void write(int b) throws IOException {
		tempFileOutputStream.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		tempFileOutputStream.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		tempFileOutputStream.write(b);
	}

	@Override
	public void close() throws IOException {
		tempFileOutputStream.close();
		copyTempFileToHostFile();
	}

	private void copyTempFileToHostFile() {
		if (logger.isDebugEnabled())
			logger.debug("Copying " + tempFile + " to " + hostFile + " after writing");
		CapturingCommandExecutionCallbackHandler capturedOutput = new CapturingCommandExecutionCallbackHandler();
		int result = hostFile.getConnection().execute(capturedOutput, "cp", tempFile.getPath(), hostFile.getPath());
		if (result != 0) {
			String errorMessage = capturedOutput.getAll();
			throw new RuntimeIOException("Cannot copy " + tempFile + " to " + hostFile + " after writing: " + errorMessage);
		}
	}

	private Logger logger = LoggerFactory.getLogger(SshSudoOutputStream.class);

}
