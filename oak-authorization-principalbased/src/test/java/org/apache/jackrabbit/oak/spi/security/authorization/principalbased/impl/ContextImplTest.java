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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
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
name|mock
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
name|ContextImplTest
implements|implements
name|Constants
block|{
specifier|private
specifier|static
specifier|final
name|Context
name|CTX
init|=
name|ContextImpl
operator|.
name|INSTANCE
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
parameter_list|,
name|boolean
name|exists
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|propertyNames
parameter_list|)
block|{
return|return
name|MockUtility
operator|.
name|mockTree
argument_list|(
name|name
argument_list|,
name|ntName
argument_list|,
name|exists
argument_list|,
name|propertyNames
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesTree
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
name|NT_REP_PRINCIPAL_ENTRY
argument_list|,
literal|true
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
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
name|NT_REP_PRINCIPAL_ENTRY
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
literal|"anyNt"
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesTree
argument_list|(
name|mockTree
argument_list|(
literal|"anyName"
argument_list|,
literal|"anyNt"
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesProperty
parameter_list|()
block|{
name|PropertyState
name|anyProperty
init|=
name|mock
argument_list|(
name|PropertyState
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|true
argument_list|)
argument_list|,
name|anyProperty
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|true
argument_list|)
argument_list|,
name|anyProperty
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesProperty
argument_list|(
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
name|NT_REP_PRINCIPAL_ENTRY
argument_list|,
literal|true
argument_list|)
argument_list|,
name|anyProperty
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesCtxRoot
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotDefinesCtxRoot
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_POLICY
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesContextRoot
argument_list|(
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesNodeLocation
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
name|NT_REP_PRINCIPAL_ENTRY
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesPropertyLocation
parameter_list|()
block|{
name|String
index|[]
name|propNames
init|=
operator|new
name|String
index|[]
block|{
name|REP_PRINCIPAL_NAME
block|,
literal|"prop1"
block|,
literal|"prop2"
block|}
decl_stmt|;
name|Tree
name|policyTree
init|=
name|MockUtility
operator|.
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
operator|+
name|REP_PRINCIPAL_POLICY
argument_list|,
name|propNames
argument_list|)
decl_stmt|;
name|Tree
name|rootTree
init|=
name|MockUtility
operator|.
name|mockTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_NAME
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_REP_ROOT
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|propNames
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rootTree
operator|.
name|getChild
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|policyTree
argument_list|)
expr_stmt|;
name|Root
name|r
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rootTree
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|propNames
control|)
block|{
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|r
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|policyTree
operator|.
name|getPath
argument_list|()
argument_list|,
name|propName
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|r
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|rootTree
operator|.
name|getPath
argument_list|()
argument_list|,
name|propName
argument_list|)
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
name|testDefinesNonExistingLocation
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|t
init|=
name|mockTree
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|,
literal|false
argument_list|,
literal|"anyResidualProperty"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|t
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"anyResidualProperty"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
name|mockTree
argument_list|(
name|REP_PRINCIPAL_POLICY
argument_list|,
name|NT_REP_PRINCIPAL_POLICY
argument_list|,
literal|false
argument_list|,
literal|"anyResidualAceName"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|t
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"anyResidualAceName"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|propNames
init|=
operator|new
name|String
index|[]
block|{
name|REP_EFFECTIVE_PATH
block|,
name|REP_PRINCIPAL_NAME
block|,
name|REP_PRIVILEGES
block|}
decl_stmt|;
name|t
operator|=
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
literal|"anyNt"
argument_list|,
literal|false
argument_list|,
name|propNames
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
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|t
argument_list|)
operator|.
name|getChild
argument_list|(
name|propName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|nodeNames
init|=
operator|new
name|String
index|[]
block|{
name|REP_PRINCIPAL_POLICY
block|,
name|REP_RESTRICTIONS
block|}
decl_stmt|;
for|for
control|(
name|String
name|nodeName
range|:
name|nodeNames
control|)
block|{
name|assertTrue
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|mockTree
argument_list|(
name|nodeName
argument_list|,
literal|"anyNt"
argument_list|,
literal|false
argument_list|)
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
name|testNotDefinesLocation
parameter_list|()
block|{
name|Tree
name|t
init|=
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
literal|"anyNt"
argument_list|,
literal|false
argument_list|,
literal|"anyResidualProperty"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|t
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"anyResidualProperty"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
name|mockTree
argument_list|(
literal|"anyEntry"
argument_list|,
literal|"anyNt"
argument_list|,
literal|true
argument_list|,
literal|"anyResidualProperty"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|t
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"anyResidualProperty"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesInternal
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesInternal
argument_list|(
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|CTX
operator|.
name|definesInternal
argument_list|(
name|when
argument_list|(
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PermissionConstants
operator|.
name|REP_PERMISSION_STORE
argument_list|)
operator|.
name|getMock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
