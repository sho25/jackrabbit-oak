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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|api
operator|.
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|SecurityProvider
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
name|action
operator|.
name|AbstractGroupAction
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
name|action
operator|.
name|AuthorizableAction
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
name|action
operator|.
name|AuthorizableActionProvider
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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

begin_comment
comment|/**  * Testing {@link ImportBehavior#IGNORE} for group import  */
end_comment

begin_class
specifier|public
class|class
name|GroupImportWithActionsBestEffortTest
extends|extends
name|AbstractImportTest
block|{
specifier|private
specifier|final
name|TestGroupAction
name|groupAction
init|=
operator|new
name|TestGroupAction
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|TestActionProvider
name|actionProvider
init|=
operator|new
name|TestActionProvider
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|actionProvider
operator|.
name|addAction
argument_list|(
name|groupAction
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportMembersBestEffort
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user1
init|=
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
literal|"user1"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|uuid1
init|=
name|getImportSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|user1
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|User
name|user2
init|=
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
literal|"user2"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|uuid2
init|=
name|getImportSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|user2
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|String
name|nonExistingUUID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|failedUUID
init|=
name|uuid1
decl_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\">"
operator|+
literal|"<sv:value>rep:AuthorizableFolder</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:multiple=\"true\" sv:type=\"WeakReference\">"
operator|+
literal|"<sv:value>"
operator|+
name|uuid1
operator|+
literal|"</sv:value>"
operator|+
literal|"<sv:value>"
operator|+
name|uuid2
operator|+
literal|"</sv:value>"
operator|+
literal|"<sv:value>"
operator|+
name|nonExistingUUID
operator|+
literal|"</sv:value>"
operator|+
literal|"<sv:value>"
operator|+
name|failedUUID
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Group
name|g1
init|=
operator|(
name|Group
operator|)
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groupAction
operator|.
name|onMembersAddedCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groupAction
operator|.
name|onMembersAddedContentIdCalled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|g1
operator|.
name|getID
argument_list|()
argument_list|,
name|groupAction
operator|.
name|group
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|user1
operator|.
name|getID
argument_list|()
argument_list|,
name|user2
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|,
name|groupAction
operator|.
name|memberIds
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|nonExistingUUID
argument_list|)
argument_list|,
name|groupAction
operator|.
name|memberContentIds
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|groupAction
operator|.
name|failedIds
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// duplicate uuids are swallowed by the set in userImporter: nonExisting#add
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTargetPath
parameter_list|()
block|{
return|return
name|GROUPPATH
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getConfigurationParameters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|userParams
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|userParams
operator|.
name|put
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|getImportBehavior
argument_list|()
argument_list|)
expr_stmt|;
name|userParams
operator|.
name|put
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_ACTION_PROVIDER
argument_list|,
name|actionProvider
argument_list|)
expr_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|userParams
argument_list|)
argument_list|)
return|;
block|}
specifier|private
class|class
name|TestGroupAction
extends|extends
name|AbstractGroupAction
block|{
name|boolean
name|onMemberAddedCalled
init|=
literal|false
decl_stmt|;
name|boolean
name|onMembersAddedCalled
init|=
literal|false
decl_stmt|;
name|boolean
name|onMembersAddedContentIdCalled
init|=
literal|false
decl_stmt|;
name|Group
name|group
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|memberIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|memberContentIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|failedIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onMemberAdded
parameter_list|(
annotation|@
name|NotNull
name|Group
name|group
parameter_list|,
annotation|@
name|NotNull
name|Authorizable
name|member
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|memberIds
operator|.
name|add
argument_list|(
name|member
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|onMemberAddedCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersAdded
parameter_list|(
annotation|@
name|NotNull
name|Group
name|group
parameter_list|,
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberIds
parameter_list|,
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|memberIds
operator|.
name|addAll
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|memberIds
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|failedIds
operator|.
name|addAll
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|failedIds
argument_list|)
argument_list|)
expr_stmt|;
name|onMembersAddedCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMembersAddedContentId
parameter_list|(
annotation|@
name|NotNull
name|Group
name|group
parameter_list|,
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|String
argument_list|>
name|memberContentIds
parameter_list|,
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|String
argument_list|>
name|failedIds
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|memberContentIds
operator|.
name|addAll
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|memberContentIds
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|failedIds
operator|.
name|addAll
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|failedIds
argument_list|)
argument_list|)
expr_stmt|;
name|onMembersAddedContentIdCalled
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|TestActionProvider
implements|implements
name|AuthorizableActionProvider
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|AuthorizableAction
argument_list|>
name|actions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
name|void
name|addAction
parameter_list|(
name|AuthorizableAction
name|action
parameter_list|)
block|{
name|actions
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
return|return
name|actions
return|;
block|}
block|}
block|}
end_class

end_unit

