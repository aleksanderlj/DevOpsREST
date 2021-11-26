import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.LoginData;
import model.Post;
import model.User;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.json.JsonObject;
import org.junit.BeforeClass;
import util.DateTypeAdapter;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IntegrationTest {
    private static final int PORT = 5001;

    @Test
    public void integrationPostTest() throws IOException {
        /* INITIALIZE */
        DateTypeAdapter myAdapter = new DateTypeAdapter();
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, myAdapter).create();
        CloseableHttpClient client = HttpClients.createDefault();

        Post postPost = new Post();
        postPost.setTitle("Hallo");
        postPost.setContent("Hello my friend");
        postPost.setImageUrl("https://media.vanityfair.com/photos/5f5156490ca7fe28f9ec3f55/16:9/w_1280,c_limit/feels-good-man-film.jpg");
        postPost.setPostDate(new Date(2020, Calendar.FEBRUARY, 1));
        postPost.setUserId(2L);

        Post updatePost = new Post();
        updatePost.setTitle("Updated Hallo");
        updatePost.setContent("Hello my friend");
        updatePost.setImageUrl("https://media.vanityfair.com/photos/5f5156490ca7fe28f9ec3f55/16:9/w_1280,c_limit/feels-good-man-film.jpg");
        updatePost.setPostDate(new Date(2020, Calendar.FEBRUARY, 1));
        updatePost.setUserId(2L);

        /* POST */
        HttpPost postRequest = new HttpPost("http://localhost:"+ PORT +"/post");
        String postJson = gson.toJson(postPost);
        StringEntity postEntity = new StringEntity(postJson);
        postRequest.setEntity(postEntity);

        CloseableHttpResponse postResponse = client.execute(postRequest);
        Assert.assertEquals(200, postResponse.getStatusLine().getStatusCode());


        /* GET ID */
        final String postId = EntityUtils.toString(postResponse.getEntity());
        HttpUriRequest getRequest1 = new HttpGet("http://localhost:"+ PORT +"/post/" + postId);
        HttpResponse getResponse1 = HttpClientBuilder.create().build().execute( getRequest1 );

        Assert.assertEquals(200, getResponse1.getStatusLine().getStatusCode());
        Post responsePost1 = gson.fromJson(EntityUtils.toString(getResponse1.getEntity()), Post.class);
        Assert.assertEquals(postPost.getTitle(), responsePost1.getTitle());

        /* UPDATE */
        updatePost.setId(Long.parseLong(postId));
        HttpPatch updateRequest = new HttpPatch("http://localhost:"+ PORT +"/post");
        String updateJson = gson.toJson(updatePost);
        StringEntity updateEntity = new StringEntity(updateJson);
        updateRequest.setEntity(updateEntity);

        CloseableHttpResponse updateResponse = client.execute(updateRequest);
        Assert.assertEquals(200, updateResponse.getStatusLine().getStatusCode());

        // Get to check post got updated
        HttpUriRequest getRequest2 = new HttpGet("http://localhost:"+ PORT +"/post/" + postId);
        HttpResponse getResponse2 = HttpClientBuilder.create().build().execute( getRequest2 );

        Assert.assertEquals(200, getResponse2.getStatusLine().getStatusCode());
        Post responsePost2 = gson.fromJson(EntityUtils.toString(getResponse2.getEntity()), Post.class);
        Assert.assertEquals(updatePost.getTitle(), responsePost2.getTitle());

        /* GET ALL */
        HttpUriRequest getAllRequest = new HttpGet("http://localhost:"+ PORT +"/post");
        HttpResponse getAllResponse = HttpClientBuilder.create().build().execute( getAllRequest );

        Assert.assertEquals(200, getAllResponse.getStatusLine().getStatusCode());
        List<Post> responseAllPost = gson.fromJson(EntityUtils.toString(getAllResponse.getEntity()), new TypeToken<List<Post>>(){}.getType());
        Assert.assertTrue(responseAllPost.size() > 0);

        /* DELETE */
        HttpDelete deleteRequest = new HttpDelete("http://localhost:"+ PORT +"/post/" + postId);
        HttpResponse deleteResponse = HttpClientBuilder.create().build().execute( deleteRequest );

        Assert.assertEquals(200, deleteResponse.getStatusLine().getStatusCode());

        client.close();
    }

    @Test
    public void integrationUserTest() throws IOException {
        /* INITIALIZE */
        DateTypeAdapter myAdapter = new DateTypeAdapter();
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, myAdapter).create();
        CloseableHttpClient client = HttpClients.createDefault();

        User postUser = new User();
        postUser.setUsername("Kenobi");
        postUser.setPassword("General");

        User updateUser = new User();
        updateUser.setUsername("Grievous");
        updateUser.setPassword("General");

        /* POST */
        HttpPost postRequest = new HttpPost("http://localhost:"+ PORT +"/user");
        String postJson = gson.toJson(postUser);
        StringEntity postEntity = new StringEntity(postJson);
        postRequest.setEntity(postEntity);

        CloseableHttpResponse postResponse = client.execute(postRequest);
        Assert.assertEquals(200, postResponse.getStatusLine().getStatusCode());

        HttpPost postTokenRequest = new HttpPost("http://localhost:"+ PORT +"/token/validate/");
        StringEntity postTokenEntity = new StringEntity(EntityUtils.toString(postResponse.getEntity()));
        postTokenRequest.setEntity(postTokenEntity);

        CloseableHttpResponse postTokenResponse = client.execute(postTokenRequest);
        Assert.assertEquals(200, postTokenResponse.getStatusLine().getStatusCode());

        /* POST user already exists */
        postResponse = client.execute(postRequest);
        Assert.assertEquals(422, postResponse.getStatusLine().getStatusCode());

        /* GET ID */
        String hej = EntityUtils.toString(postTokenResponse.getEntity());
        JsonObject hehe = new JsonObject(hej);
        String userId = Long.toString(hehe.toBsonDocument().get("body").asDocument().getInt32("id").getValue());

        System.out.println(userId);
        HttpUriRequest getRequest1 = new HttpGet("http://localhost:"+ PORT +"/user/" + userId);
        HttpResponse getResponse1 = HttpClientBuilder.create().build().execute( getRequest1 );

        Assert.assertEquals(200, getResponse1.getStatusLine().getStatusCode());
        User responsePost1 = gson.fromJson(EntityUtils.toString(getResponse1.getEntity()), User.class);
        Assert.assertEquals(postUser.getUsername(), responsePost1.getUsername());

        /* UPDATE */
        updateUser.setId(Long.parseLong(userId));
        HttpPatch updateRequest = new HttpPatch("http://localhost:"+ PORT +"/user");
        String updateJson = gson.toJson(updateUser);
        StringEntity updateEntity = new StringEntity(updateJson);
        updateRequest.setEntity(updateEntity);

        CloseableHttpResponse updateResponse = client.execute(updateRequest);
        Assert.assertEquals(200, updateResponse.getStatusLine().getStatusCode());

        /* UPDATE invalidpayload */
        updateUser.setId(null);
        updateRequest.setEntity(new StringEntity(gson.toJson(updateUser)));
        updateResponse = client.execute(updateRequest);
        Assert.assertEquals(400, updateResponse.getStatusLine().getStatusCode());

        // Get to check post got updated
        HttpUriRequest getRequest2 = new HttpGet("http://localhost:"+ PORT +"/user/" + userId);
        HttpResponse getResponse2 = HttpClientBuilder.create().build().execute( getRequest2 );

        Assert.assertEquals(200, getResponse2.getStatusLine().getStatusCode());
        User responsePost2 = gson.fromJson(EntityUtils.toString(getResponse2.getEntity()), User.class);
        Assert.assertEquals(updateUser.getUsername(), responsePost2.getUsername());

        /* GET ALL */
        HttpUriRequest getAllRequest = new HttpGet("http://localhost:"+ PORT +"/user");
        HttpResponse getAllResponse = HttpClientBuilder.create().build().execute( getAllRequest );

        Assert.assertEquals(200, getAllResponse.getStatusLine().getStatusCode());
        List<User> responseAllUser = gson.fromJson(EntityUtils.toString(getAllResponse.getEntity()), new TypeToken<List<User>>(){}.getType());
        Assert.assertTrue(responseAllUser.size() > 0);

        /* LOGIN success */
        HttpPost loginRequest = new HttpPost("http://localhost:"+ PORT +"/user/login");
        LoginData loginData = new LoginData();
        loginData.setUsername(updateUser.getUsername());
        loginData.setPassword(updateUser.getPassword());
        String loginJson = gson.toJson(loginData);
        loginRequest.setEntity(new StringEntity(loginJson));
        HttpResponse loginResponse = HttpClientBuilder.create().build().execute( loginRequest );
        Assert.assertEquals(200, loginResponse.getStatusLine().getStatusCode());

        /* LOGIN fail */
        loginRequest = new HttpPost("http://localhost:"+ PORT +"/user/login");
        loginData.setUsername(updateUser.getUsername());
        loginData.setPassword("oogabooga");
        loginJson = gson.toJson(loginData);
        loginRequest.setEntity(new StringEntity(loginJson));
        loginResponse = HttpClientBuilder.create().build().execute( loginRequest );
        Assert.assertEquals(401, loginResponse.getStatusLine().getStatusCode());

        /* DELETE */
        HttpDelete deleteRequest = new HttpDelete("http://localhost:"+ PORT +"/user/" + userId);
        HttpResponse deleteResponse = HttpClientBuilder.create().build().execute( deleteRequest );

        Assert.assertEquals(200, deleteResponse.getStatusLine().getStatusCode());

        client.close();
    }

    @BeforeClass
    public static void initializeJavalin() {
        Main.initializeJavalin(PORT);
    }
}
