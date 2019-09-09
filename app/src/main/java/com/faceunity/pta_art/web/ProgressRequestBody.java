package com.faceunity.pta_art.web;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by tujh on 2018/12/11.
 */
public class ProgressRequestBody extends RequestBody {

    protected RequestBody delegate;
    private UploadProgressListener uploadProgressListener;
    private ProgressSink progressSink;

    public ProgressRequestBody(RequestBody delegate, UploadProgressListener uploadProgressListener) {
        this.uploadProgressListener = uploadProgressListener;
        this.delegate = delegate;
    }

    public static interface UploadProgressListener {
        void onUploadRequestProgress(long byteWritten, long contentLength);
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        progressSink = new ProgressSink(sink);
        BufferedSink bufferedSink = Okio.buffer(progressSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class ProgressSink extends ForwardingSink {
        private long bytesWritten;

        public ProgressSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            uploadProgressListener.onUploadRequestProgress(bytesWritten, contentLength());
        }
    }
}

