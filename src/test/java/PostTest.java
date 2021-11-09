import model.Post;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PostTest {

    @Test
    public void insertPost() throws IOException {
        Post post = new Post();
        post.setTitle("Goddag");
        post.setContent("Hello my friend");
        post.setImageUrl("https://media.vanityfair.com/photos/5f5156490ca7fe28f9ec3f55/16:9/w_1280,c_limit/feels-good-man-film.jpg");
        post.setPostDate(new Date(2020, Calendar.FEBRUARY, 1));
        post.setUserId(2L);

        HttpUriRequest request = new HttpGet("localhost:5000");
        HttpResponse response = HttpClientBuilder.create().build().execute( request );

        //Post

        Assert.assertEquals("Goddag", post.getTitle());
    }
}
