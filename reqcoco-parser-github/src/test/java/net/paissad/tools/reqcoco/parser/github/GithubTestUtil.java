package net.paissad.tools.reqcoco.parser.github;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.junit.Assume;

public interface GithubTestUtil {

    String GITHUB_ROOT_URL = "https://github.com";

    public static void assumePublicApiReachable() {
        Assume.assumeTrue("The Github public API '" + GITHUB_ROOT_URL + "' is not reachable. This test will be ignored !", isPublicApiReachable());
    }

    /**
     * @return <code>true</code> if the HTTP call to root url is successful, <code>false</code> otherwise.
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static boolean isPublicApiReachable() {

        final ResponseHandler<Boolean> handler = new ResponseHandler<Boolean>() {
            @Override
            public Boolean handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                final int statusCode = response.getStatusLine().getStatusCode();
                return statusCode >= 200 && statusCode < 300;
            }
        };
        try {
            return Request.Get(GITHUB_ROOT_URL).execute().handleResponse(handler);
        } catch (IOException e) {
            return false;
        }
    }

}
