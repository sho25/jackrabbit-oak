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
name|Collection
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMap
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
name|Iterables
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
name|JcrConstants
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
name|PropertyState
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
name|api
operator|.
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|TreeProviderService
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
name|plugins
operator|.
name|tree
operator|.
name|TreeLocation
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
name|plugins
operator|.
name|tree
operator|.
name|TreeProvider
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
name|Context
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|UserContextTest
implements|implements
name|UserConstants
block|{
specifier|private
specifier|final
name|Context
name|ctx
init|=
name|UserContext
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|Tree
name|mockTree
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|String
name|ntName
parameter_list|)
block|{
name|Tree
name|t
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|ntName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesUserProperty
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|propNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|USER_PROPERTY_NAMES
argument_list|)
decl_stmt|;
name|propNames
operator|.
name|removeAll
argument_list|(
name|GROUP_PROPERTY_NAMES
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|propNames
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_GROUP
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesGroupProperty
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|propNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|GROUP_PROPERTY_NAMES
argument_list|)
decl_stmt|;
name|propNames
operator|.
name|removeAll
argument_list|(
name|USER_PROPERTY_NAMES
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|propNames
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_GROUP
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesAuthorizableProperty
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
operator|new
name|String
index|[]
block|{
name|REP_AUTHORIZABLE_ID
block|,
name|REP_PRINCIPAL_NAME
block|}
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_GROUP
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesPasswordNodeProperty
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
name|PWD_PROPERTY_NAMES
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_PASSWORD
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_GROUP
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesMemberRefProperty
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_MEMBERS
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_MEMBER_REFERENCES
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_GROUP
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesMemberProperty
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
operator|new
name|String
index|[]
block|{
literal|"any"
block|,
literal|"prop"
block|}
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_MEMBERS
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_GROUP
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"nodeName"
argument_list|,
name|NT_REP_SYSTEM_USER
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameNotDefinesProperty
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
operator|new
name|String
index|[]
block|{
literal|"anyName"
block|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
block|}
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|boolean
name|defines
init|=
name|NT_REP_MEMBERS
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|defines
argument_list|,
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentNotDefinesProperty
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|USER_PROPERTY_NAMES
argument_list|,
name|GROUP_PROPERTY_NAMES
argument_list|)
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|ntName
range|:
operator|new
name|String
index|[]
block|{
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
block|,
name|NT_REP_AUTHORIZABLE_FOLDER
block|}
control|)
block|{
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesContextRoot
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotDefinesContextRoot
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
block|,
name|JcrConstants
operator|.
name|NT_BASE
block|,
name|NodeTypeConstants
operator|.
name|NT_REP_SYSTEM
block|,
name|NodeTypeConstants
operator|.
name|NT_REP_ROOT
block|,
name|NT_REP_AUTHORIZABLE_FOLDER
block|}
control|)
block|{
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesTree
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyNotDefinesTree
parameter_list|()
block|{
name|TreeProvider
name|treeProvider
init|=
operator|new
name|TreeProviderService
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesTree
argument_list|(
name|treeProvider
operator|.
name|createReadOnlyTree
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotDefinesTree
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
block|,
name|JcrConstants
operator|.
name|NT_BASE
block|,
name|NodeTypeConstants
operator|.
name|NT_REP_SYSTEM
block|,
name|NodeTypeConstants
operator|.
name|NT_REP_ROOT
block|,
name|NT_REP_AUTHORIZABLE_FOLDER
block|}
control|)
block|{
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesLocation
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|Tree
name|t
init|=
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|location
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyDefinesLocation
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|m
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NT_REP_GROUP
argument_list|,
name|GROUP_PROPERTY_NAMES
argument_list|,
name|NT_REP_USER
argument_list|,
name|USER_PROPERTY_NAMES
argument_list|,
name|NT_REP_PASSWORD
argument_list|,
name|PWD_PROPERTY_NAMES
argument_list|,
name|NT_REP_MEMBER_REFERENCES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|REP_MEMBERS
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|ntName
range|:
name|m
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Tree
name|t
init|=
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|location
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|m
operator|.
name|get
argument_list|(
name|ntName
argument_list|)
control|)
block|{
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|propName
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getProperty
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|property
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"anyName"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getProperty
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExistingTreeDefinesLocation
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|Tree
name|t
init|=
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|location
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/somePath"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonExistingTreeDefinesLocation2
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|USER_PROPERTY_NAMES
argument_list|,
name|GROUP_PROPERTY_NAMES
argument_list|)
control|)
block|{
name|String
name|path
init|=
literal|"/some/path/endingWith/reservedName/"
operator|+
name|name
decl_stmt|;
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|Tree
name|t
init|=
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
decl_stmt|;
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getTree
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|location
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoTreeDefinesLocationReservedEnding
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|USER_PROPERTY_NAMES
argument_list|,
name|GROUP_PROPERTY_NAMES
argument_list|)
control|)
block|{
name|String
name|path
init|=
literal|"/some/path/endingWith/reservedName/"
operator|+
name|name
decl_stmt|;
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
argument_list|,
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoTreeDefinesLocationIntermediate
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
operator|new
name|String
index|[]
block|{
name|REP_MEMBERS
block|,
name|REP_MEMBERS_LIST
block|,
name|REP_PWD
block|}
control|)
block|{
name|String
name|path
init|=
literal|"/some/path/with/intermediate/"
operator|+
name|name
operator|+
literal|"/reserved"
decl_stmt|;
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
argument_list|,
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotDefinesLocation
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|,
literal|"/content"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|TreeLocation
name|location
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TreeLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
argument_list|,
name|ctx
operator|.
name|definesLocation
argument_list|(
name|location
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesInternal
parameter_list|()
block|{
for|for
control|(
name|String
name|ntName
range|:
name|NT_NAMES
control|)
block|{
name|assertFalse
argument_list|(
name|ctx
operator|.
name|definesInternal
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
name|ntName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

