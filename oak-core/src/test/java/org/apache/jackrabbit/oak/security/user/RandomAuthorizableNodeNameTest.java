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
name|Authorizable
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
name|Group
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
name|AbstractSecurityTest
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
name|ConfigurationParameters
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
name|principal
operator|.
name|PrincipalImpl
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
name|AuthorizableNodeName
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
name|UserConfiguration
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
name|UserConstants
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|RandomAuthorizableNodeNameTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|final
name|String
name|id
init|=
literal|"id"
decl_stmt|;
specifier|private
name|AuthorizableNodeName
name|nameGenerator
init|=
operator|new
name|RandomAuthorizableNodeName
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Authorizable
name|a
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|userConfig
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_NODE_NAME
argument_list|,
name|nameGenerator
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|userConfig
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertNodeName
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|assertEquals
argument_list|(
name|id
argument_list|,
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|authorizable
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|id
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RandomAuthorizableNodeName
operator|.
name|DEFAULT_LENGTH
argument_list|,
name|tree
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertNodeName
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|,
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|assertEquals
argument_list|(
name|id
argument_list|,
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|authorizable
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|id
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RandomAuthorizableNodeName
operator|.
name|DEFAULT_LENGTH
argument_list|,
name|tree
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|end
init|=
literal|'/'
operator|+
name|relPath
operator|+
literal|'/'
operator|+
name|tree
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|endsWith
argument_list|(
name|end
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGenerateNodeName
parameter_list|()
block|{
name|String
name|nodeName
init|=
name|nameGenerator
operator|.
name|generateNodeName
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"id"
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RandomAuthorizableNodeName
operator|.
name|DEFAULT_LENGTH
argument_list|,
name|nodeName
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nodeName
operator|.
name|equals
argument_list|(
name|nameGenerator
operator|.
name|generateNodeName
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUser
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|id
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNodeName
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Authorizable
name|authorizable
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNodeName
argument_list|(
name|authorizable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateUserWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
name|id
argument_list|,
literal|"pw"
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
name|id
argument_list|)
argument_list|,
literal|"a/b"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNodeName
argument_list|(
name|user
argument_list|,
literal|"a/b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|group
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNodeName
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|Authorizable
name|authorizable
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertNodeName
argument_list|(
name|authorizable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateGroupWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|group
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
name|id
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
name|id
argument_list|)
argument_list|,
literal|"a/b"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNodeName
argument_list|(
name|group
argument_list|,
literal|"a/b"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

