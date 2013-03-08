begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|jcr
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * UserTest...  */
end_comment

begin_class
specifier|public
class|class
name|UserTest
extends|extends
name|AbstractUserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIsUser
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|//        Authorizable authorizable = userMgr.getAuthorizable(user.getID());
comment|//        assertTrue(authorizable instanceof User);
block|}
comment|//    @Test
comment|//    public void testIsGroup() throws RepositoryException {
comment|//        assertFalse(user.isGroup());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testGetId() throws NotExecutableException, RepositoryException {
comment|//        assertNotNull(user.getID());
comment|//        assertNotNull(userMgr.getAuthorizable(user.getID()).getID());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testGetPrincipal() throws RepositoryException, NotExecutableException {
comment|//        assertNotNull(user.getPrincipal());
comment|//        assertNotNull(userMgr.getAuthorizable(user.getID()).getPrincipal());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testGetPath() throws RepositoryException, NotExecutableException {
comment|//        assertNotNull(user.getPath());
comment|//        assertNotNull(userMgr.getAuthorizable(user.getID()).getPath());
comment|//        try {
comment|//            assertEquals(getNode(user, superuser).getPath(), user.getPath());
comment|//        } catch (UnsupportedRepositoryOperationException e) {
comment|//            // ok.
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testIsAdmin() throws NotExecutableException, RepositoryException {
comment|//        assertFalse(user.isAdmin());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testChangePasswordNull() throws RepositoryException, NotExecutableException {
comment|//        // invalid 'null' pw string
comment|//        try {
comment|//            user.changePassword(null);
comment|//            superuser.save();
comment|//            fail("invalid pw null");
comment|//        } catch (Exception e) {
comment|//            // success
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testChangePassword() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//            String hash = getNode(user, superuser).getProperty(UserConstants.REP_PASSWORD).getString();
comment|//
comment|//            user.changePassword("changed");
comment|//            superuser.save();
comment|//
comment|//            assertFalse(hash.equals(getNode(user, superuser).getProperty(UserConstants.REP_PASSWORD).getString()));
comment|//        } catch (Exception e) {
comment|//            // success
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testChangePasswordWithInvalidOldPassword() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//            user.changePassword("changed", "wrongOldPw");
comment|//            superuser.save();
comment|//            fail("old password didn't match -> changePassword(String,String) should fail.");
comment|//        } catch (RepositoryException e) {
comment|//            // success.
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testChangePasswordWithOldPassword() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//            String hash = getNode(user, superuser).getProperty(UserConstants.REP_PASSWORD).getString();
comment|//
comment|//            user.changePassword("changed", testPw);
comment|//            superuser.save();
comment|//
comment|//            assertFalse(hash.equals(getNode(user, superuser).getProperty(UserConstants.REP_PASSWORD).getString()));
comment|//        } finally {
comment|//            user.changePassword(testPw);
comment|//            superuser.save();
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testLoginAfterChangePassword() throws RepositoryException {
comment|//        user.changePassword("changed");
comment|//        superuser.save();
comment|//
comment|//        // make sure the user can login with the new pw
comment|//        Session s = getHelper().getRepository().login(new SimpleCredentials(user.getID(), "changed".toCharArray()));
comment|//        s.logout();
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testLoginAfterChangePassword2() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//
comment|//            user.changePassword("changed", testPw);
comment|//            superuser.save();
comment|//
comment|//            // make sure the user can login with the new pw
comment|//            Session s = getHelper().getRepository().login(new SimpleCredentials(user.getID(), "changed".toCharArray()));
comment|//            s.logout();
comment|//        } finally {
comment|//            user.changePassword(testPw);
comment|//            superuser.save();
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testLoginWithOldPassword() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//            user.changePassword("changed");
comment|//            superuser.save();
comment|//
comment|//            Session s = getHelper().getRepository().login(new SimpleCredentials(user.getID(), testPw.toCharArray()));
comment|//            s.logout();
comment|//            fail("user pw has changed. login must fail.");
comment|//        } catch (LoginException e) {
comment|//            // success
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testLoginWithOldPassword2() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//            user.changePassword("changed", testPw);
comment|//            superuser.save();
comment|//
comment|//            Session s = getHelper().getRepository().login(new SimpleCredentials(user.getID(), testPw.toCharArray()));
comment|//            s.logout();
comment|//            fail("superuser pw has changed. login must fail.");
comment|//        } catch (LoginException e) {
comment|//            // success
comment|//        } finally {
comment|//            user.changePassword(testPw);
comment|//            superuser.save();
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testEnabledByDefault() throws Exception {
comment|//        // by default a user isn't disabled
comment|//        assertFalse(user.isDisabled());
comment|//        assertNull(user.getDisabledReason());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testDisable() throws Exception {
comment|//        String reason = "readonly user is disabled!";
comment|//        user.disable(reason);
comment|//        superuser.save();
comment|//        assertTrue(user.isDisabled());
comment|//        assertEquals(reason, user.getDisabledReason());
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testAccessDisabledUser() throws Exception {
comment|//        user.disable("readonly user is disabled!");
comment|//        superuser.save();
comment|//
comment|//        // user must still be retrievable from user manager
comment|//        assertNotNull(getUserManager(superuser).getAuthorizable(user.getID()));
comment|//        // ... and from principal manager as well
comment|//        assertTrue(((JackrabbitSession) superuser).getPrincipalManager().hasPrincipal(user.getPrincipal().getName()));
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testAccessPrincipalOfDisabledUser()  throws Exception {
comment|//        user.disable("readonly user is disabled!");
comment|//        superuser.save();
comment|//
comment|//        Principal principal = user.getPrincipal();
comment|//        assertTrue(((JackrabbitSession) superuser).getPrincipalManager().hasPrincipal(principal.getName()));
comment|//        assertEquals(principal, ((JackrabbitSession) superuser).getPrincipalManager().getPrincipal(principal.getName()));
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testEnableUser() throws Exception {
comment|//        user.disable("readonly user is disabled!");
comment|//        superuser.save();
comment|//
comment|//        // enable user again
comment|//        user.disable(null);
comment|//        superuser.save();
comment|//        assertFalse(user.isDisabled());
comment|//        assertNull(user.getDisabledReason());
comment|//
comment|//        // -> login must succeed again
comment|//        getHelper().getRepository().login(new SimpleCredentials(user.getID(), "pw".toCharArray())).logout();
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testLoginDisabledUser() throws Exception {
comment|//        user.disable("readonly user is disabled!");
comment|//        superuser.save();
comment|//
comment|//        // -> login must fail
comment|//        try {
comment|//            Session ss = getHelper().getRepository().login(new SimpleCredentials(user.getID(), "pw".toCharArray()));
comment|//            ss.logout();
comment|//            fail("A disabled user must not be allowed to login any more");
comment|//        } catch (LoginException e) {
comment|//            // success
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testImpersonateDisabledUser() throws Exception {
comment|//        user.disable("readonly user is disabled!");
comment|//        superuser.save();
comment|//
comment|//        // -> impersonating this user must fail
comment|//        try {
comment|//            Session ss = superuser.impersonate(new SimpleCredentials(user.getID(), new char[0]));
comment|//            ss.logout();
comment|//            fail("A disabled user cannot be impersonated any more.");
comment|//        } catch (LoginException e) {
comment|//            // success
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testLoginWithGetCredentials() throws RepositoryException, NotExecutableException {
comment|//        try {
comment|//            Credentials creds = user.getCredentials();
comment|//            Session s = getHelper().getRepository().login(creds);
comment|//            s.logout();
comment|//            fail("Login using credentials exposed on user must fail.");
comment|//        } catch (UnsupportedRepositoryOperationException e) {
comment|//            throw new NotExecutableException(e.getMessage());
comment|//        } catch (LoginException e) {
comment|//            // success
comment|//        }
comment|//    }
block|}
end_class

end_unit

