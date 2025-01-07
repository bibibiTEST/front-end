package com.example.upload;

import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class LocalServer extends NanoHTTPD {

    private final File uploadDir;

    public LocalServer(int port, File uploadDir) throws IOException {
        super(port);
        this.uploadDir = uploadDir;
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Local server started on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (Method.POST.equals(session.getMethod())) {
            try {
                Map<String, String> files = new java.util.HashMap<>();
                session.parseBody(files);
                String tempFilePath = files.get("file");
                File tempFile = new File(tempFilePath);
                File destFile = new File(uploadDir, tempFile.getName());

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    fos.write(java.nio.file.Files.readAllBytes(tempFile.toPath()));
                }

                return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT,
                        "File uploaded to: " + destFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error: " + e.getMessage());
            }
        }
        return newFixedLengthResponse("Server is running...");
    }
}
