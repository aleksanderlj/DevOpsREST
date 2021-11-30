import com.mongodb.MongoWriteException;
import controller.JWTController;
import controller.LikeController;
import controller.PostController;
import controller.UserController;
import dao.CounterDAO;
import exception.InvalidPayloadException;
import exception.NotAuthorizedException;
import exception.ResourceConflictException;
import io.javalin.http.Context;
import model.LoginData;
import model.Post;
import model.User;
import mongo.SingleDinkleMan;
import org.bson.Document;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import token.JWTHandler;
import util.PropFile;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UnitTest {

    private Context ctx = mock(Context.class);
    private static String validJWT;

    @BeforeClass
    public static void initialize() throws UnknownHostException {
        SingleDinkleMan.instance().getCollection("users").deleteMany(new Document());
        SingleDinkleMan.instance().getCollection("posts").deleteMany(new Document());
        User u = new User();
        u.setId(1L);
        validJWT = JWTHandler.generateJwtToken(u);
    }

    // Post
    @Test
    public void post_Insert() throws Exception {
        Post p = new Post();
        p.setContent("TestContent");
        p.setImageUrl("https://i.imgur.com/1OdF5k6.jpeg");
        p.setTitle("TestTitle");
        p.setUserId(1L);
        p.setPostDate(Calendar.getInstance().getTime());
        when(ctx.bodyAsClass(Post.class)).thenReturn(p);
        when(ctx.header("Authorization")).thenReturn(validJWT);
        PostController.insertPost.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void post_Delete() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        PostController.deletePost.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void post_Update() throws Exception {
        Post p = new Post();
        p.setTitle("TestTitleUpdated");
        p.setId(1L);
        p.setPostDate(Calendar.getInstance().getTime());
        when(ctx.header("Authorization")).thenReturn(validJWT);
        when(ctx.bodyAsClass(Post.class)).thenReturn(p);
        PostController.updatePost.handle(ctx);
        verify(ctx).status(200);
    }

    @Test(expected = InvalidPayloadException.class)
    public void post_Update_noId() throws Exception {
        Post p = new Post();
        p.setTitle("TestTitleUpdated");
        p.setPostDate(Calendar.getInstance().getTime());
        when(ctx.bodyAsClass(Post.class)).thenReturn(p);
        PostController.updatePost.handle(ctx);
    }

    @Test
    public void post_FetchById() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        PostController.fetchById.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void post_FetchAll() throws Exception {
        PostController.fetchAll.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void post_FetchByUserId() throws Exception {
        when(ctx.pathParam("userId")).thenReturn("1");
        PostController.fetchByUserId.handle(ctx);
        verify(ctx).status(200);
    }

    // User
    @Test
    public void user_FetchByQuery() throws Exception {
        when(ctx.queryParam("name")).thenReturn("Username");
        UserController.fetchByQuery.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void user_FetchById() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        UserController.fetchById.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void user_InsertUser() throws Exception {
        User u = new User();
        u.setUsername(UUID.randomUUID().toString());
        u.setPassword("Password");
        when(ctx.bodyAsClass(User.class)).thenReturn(u);
        UserController.insertUser.handle(ctx);
        verify(ctx).status(200);
    }

    @Test(expected = ResourceConflictException.class)
    public void user_InsertUser_AlreadyExists() throws Exception {
        User u = new User();
        u.setUsername("Username");
        u.setPassword("Password");
        when(ctx.bodyAsClass(User.class)).thenReturn(u);
        UserController.insertUser.handle(ctx);
        UserController.insertUser.handle(ctx);
    }

    @Test
    public void user_Delete() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.header("Authorization")).thenReturn(validJWT);
        UserController.deleteUser.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void user_Update() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setDisplayName("DiplayNameUpdate");
        when(ctx.bodyAsClass(User.class)).thenReturn(u);
        when(ctx.header("Authorization")).thenReturn(validJWT);
        UserController.updateUser.handle(ctx);
        verify(ctx).status(200);
    }

    @Test(expected = InvalidPayloadException.class)
    public void user_Update_noId() throws Exception {
        User u = new User();
        u.setDisplayName("DiplayNameUpdate");
        when(ctx.header("Authorization")).thenReturn(validJWT);
        when(ctx.bodyAsClass(User.class)).thenReturn(u);
        UserController.updateUser.handle(ctx);
    }

    @Test
    public void user_Login() throws Exception {
        LoginData ld = new LoginData();
        ld.setUsername("Username");
        ld.setPassword("Password");
        when(ctx.bodyAsClass(LoginData.class)).thenReturn(ld);
        UserController.login.handle(ctx);
        verify(ctx).status(200);
    }

    @Test(expected = NotAuthorizedException.class)
    public void user_Login_notAuthorizedUsername() throws Exception {
        LoginData ld = new LoginData();
        ld.setUsername("Something that doesn't exist");
        ld.setPassword("Password");
        when(ctx.bodyAsClass(LoginData.class)).thenReturn(ld);
        UserController.login.handle(ctx);
    }

    @Test(expected = NotAuthorizedException.class)
    public void user_Login_notAuthorizedPassword() throws Exception {
        LoginData ld = new LoginData();
        ld.setUsername("Username");
        ld.setPassword("Wrong Password");
        when(ctx.bodyAsClass(LoginData.class)).thenReturn(ld);
        UserController.login.handle(ctx);
    }

    @Test(expected = Exception.class)
    public void user_GoogleLogin_invalidGoogleToken() throws Exception {
        UserController.googleLogin.handle(ctx);
    }

    // Like
    @Test
    public void like_LikePost() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.header("Authorization")).thenReturn(validJWT);
        LikeController.likePost.handle(ctx);
        verify(ctx).status(200);
    }

    @Test(expected = Exception.class)
    public void like_LikePost_invalidToken() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.header("Authorization")).thenReturn("invalidJWT");
        LikeController.likePost.handle(ctx);
    }

    @Test
    public void like_UnlikePost() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.header("Authorization")).thenReturn(validJWT);
        LikeController.unlikePost.handle(ctx);
        verify(ctx).status(200);
    }

    @Test(expected = Exception.class)
    public void like_UnlikePost_invalidToken() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.header("Authorization")).thenReturn("invalidJWT");
        LikeController.unlikePost.handle(ctx);
    }

    @Test
    public void like_GetPostLikeCount() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        LikeController.getPostLikeCount.handle(ctx);
        verify(ctx).status(200);
    }

    @Test
    public void like_GetPostLikeStatus() throws Exception {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.header("Authorization")).thenReturn(validJWT);
        LikeController.getLikeStatus.handle(ctx);
        verify(ctx).status(200);
    }

    // JWT
    @Test
    public void jwt_ValidateAndDecode() throws Exception {
        when(ctx.body()).thenReturn(validJWT);
        JWTController.decode.handle(ctx);
        verify(ctx).status(200);
    }

    // Miscellaneous tests to get more code coverage
    @Test
    public void propfile_setProperty() throws Exception {
        PropFile.setProperty("test", "value");
    }

    @Test
    public void propfile_getProperty() throws Exception {
        PropFile.getProperty("test");
    }

    @Test
    public void propfile_getInvalidProperty() throws Exception {
        PropFile.getProperty("notpresent");
    }

    @Test(expected = Exception.class)
    public void counters_initialize_duplicateError() throws UnknownHostException {
        CounterDAO.initiateCounters();
    }

}
