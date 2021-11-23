import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Post;
import util.DateTypeAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PostTest {

    @Test
    public void insertPost() throws IOException {
        DateTypeAdapter myAdapter = new DateTypeAdapter();
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, myAdapter).create();

        Post post = new Post();
        post.setTitle("Goddag");
        post.setContent("Hello my friend");
        post.setImageUrl("https://media.vanityfair.com/photos/5f5156490ca7fe28f9ec3f55/16:9/w_1280,c_limit/feels-good-man-film.jpg");
        post.setPostDate(new Date(2020, Calendar.FEBRUARY, 1));
        post.setUserId(2L);

        HttpUriRequest request = new HttpGet("http://localhost:5000/post/1");
        HttpResponse response = HttpClientBuilder.create().build().execute( request );

        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

        Post responsePost = gson.fromJson(EntityUtils.toString(response.getEntity()), Post.class);

        Assert.assertEquals(post.getTitle(), responsePost.getTitle());
    }
}
