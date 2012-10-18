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
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|AbstractOakTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|ContentRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Tree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|util
operator|.
name|PasswordUtility
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * UserManagerImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|UserManagerImplTest
extends|extends
name|AbstractOakTest
block|{
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
comment|//    @Test
comment|//    public void testSetPassword() throws Exception {
comment|//        UserManagerImpl userMgr = createUserManager();
comment|//        User user = userMgr.createUser("a", "pw");
comment|//
comment|//        List<String> pwds = new ArrayList<String>();
comment|//        pwds.add("pw");
comment|//        pwds.add("");
comment|//        pwds.add("{sha1}pw");
comment|//
comment|//        for (String pw : pwds) {
comment|//            user.setPassword(user, pw, true);
comment|//            String pwHash = up.getPasswordHash(user);
comment|//            assertNotNull(pwHash);
comment|//            assertTrue(PasswordUtility.isSame(pwHash, pw));
comment|//        }
comment|//
comment|//        for (String pw : pwds) {
comment|//            up.setPassword(user, pw, false);
comment|//            String pwHash = up.getPasswordHash(user);
comment|//            assertNotNull(pwHash);
comment|//            if (!pw.startsWith("{")) {
comment|//                assertTrue(PasswordUtility.isSame(pwHash, pw));
comment|//            } else {
comment|//                assertFalse(PasswordUtility.isSame(pwHash, pw));
comment|//                assertEquals(pw, pwHash);
comment|//            }
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void setPasswordNull() throws Exception {
comment|//        UserProviderImpl up = createUserProvider();
comment|//        Tree user = up.createUser("a", null);
comment|//
comment|//        try {
comment|//            up.setPassword(user, null, true);
comment|//            fail("setting null password should fail");
comment|//        } catch (IllegalArgumentException e) {
comment|//            // expected
comment|//        }
comment|//
comment|//        try {
comment|//            up.setPassword(user, null, false);
comment|//            fail("setting null password should fail");
comment|//        } catch (IllegalArgumentException e) {
comment|//            // expected
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testGetPasswordHash() throws Exception {
comment|//        UserProviderImpl up = createUserProvider();
comment|//        Tree user = up.createUser("a", null);
comment|//
comment|//        assertNull(up.getPasswordHash(user));
comment|//    }
block|}
end_class

end_unit

