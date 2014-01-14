begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|upgrade
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceRegistry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeTemplate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|JackrabbitSession
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
name|JackrabbitWorkspace
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
name|authorization
operator|.
name|PrivilegeManager
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
name|index
operator|.
name|IndexConstants
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|RepositoryUpgradeTest
extends|extends
name|AbstractRepositoryUpgradeTest
block|{
specifier|private
specifier|static
specifier|final
name|Calendar
name|DATE
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|BINARY
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
static|static
block|{
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|BINARY
argument_list|)
expr_stmt|;
block|}
comment|// needs to be static because the content is created during the @BeforeClass phase
specifier|private
specifier|static
name|String
name|testNodeIdentifier
decl_stmt|;
specifier|protected
name|void
name|createSourceContent
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
decl_stmt|;
try|try
block|{
name|JackrabbitWorkspace
name|workspace
init|=
operator|(
name|JackrabbitWorkspace
operator|)
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|NamespaceRegistry
name|registry
init|=
name|workspace
operator|.
name|getNamespaceRegistry
argument_list|()
decl_stmt|;
name|registry
operator|.
name|registerNamespace
argument_list|(
literal|"test"
argument_list|,
literal|"http://www.example.org/"
argument_list|)
expr_stmt|;
name|PrivilegeManager
name|privilegeManager
init|=
name|workspace
operator|.
name|getPrivilegeManager
argument_list|()
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:privilege"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"test:aggregate"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:read"
block|,
literal|"test:privilege"
block|}
argument_list|)
expr_stmt|;
name|NodeTypeManager
name|nodeTypeManager
init|=
name|workspace
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|NodeTypeTemplate
name|template
init|=
name|nodeTypeManager
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|template
operator|.
name|setName
argument_list|(
literal|"test:unstructured"
argument_list|)
expr_stmt|;
name|template
operator|.
name|setDeclaredSuperTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"nt:unstructured"
block|}
argument_list|)
expr_stmt|;
name|nodeTypeManager
operator|.
name|registerNodeType
argument_list|(
name|template
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|referenceable
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"referenceable"
argument_list|,
literal|"test:unstructured"
argument_list|)
decl_stmt|;
name|referenceable
operator|.
name|addMixin
argument_list|(
name|NodeType
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|Node
name|referenceable2
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"referenceable2"
argument_list|,
literal|"test:unstructured"
argument_list|)
decl_stmt|;
name|referenceable2
operator|.
name|addMixin
argument_list|(
name|NodeType
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|testNodeIdentifier
operator|=
name|referenceable
operator|.
name|getIdentifier
argument_list|()
expr_stmt|;
name|Node
name|properties
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"properties"
argument_list|,
literal|"test:unstructured"
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"boolean"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Binary
name|binary
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createBinary
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|BINARY
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"binary"
argument_list|,
name|binary
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|binary
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|properties
operator|.
name|setProperty
argument_list|(
literal|"date"
argument_list|,
name|DATE
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"decimal"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
literal|123
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"double"
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"long"
argument_list|,
literal|9876543210L
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"reference"
argument_list|,
name|referenceable
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"weak_reference"
argument_list|,
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|referenceable
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"mv_reference"
argument_list|,
operator|new
name|Value
index|[]
block|{
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|referenceable2
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"mv_weak_reference"
argument_list|,
operator|new
name|Value
index|[]
block|{
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|referenceable2
argument_list|,
literal|true
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"string"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"multiple"
argument_list|,
literal|"a,b,c"
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|binary
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"binary"
argument_list|)
operator|.
name|getBinary
argument_list|()
expr_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|binary
operator|.
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|byte
name|aBINARY
range|:
name|BINARY
control|)
block|{
name|assertEquals
argument_list|(
name|aBINARY
argument_list|,
operator|(
name|byte
operator|)
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|binary
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyNameSpaces
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"http://www.example.org/"
argument_list|,
name|session
operator|.
name|getNamespaceURI
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyCustomPrivileges
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitSession
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|JackrabbitWorkspace
name|workspace
init|=
operator|(
name|JackrabbitWorkspace
operator|)
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|PrivilegeManager
name|manager
init|=
name|workspace
operator|.
name|getPrivilegeManager
argument_list|()
decl_stmt|;
name|Privilege
name|privilege
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"test:privilege"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|privilege
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|privilege
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|privilege
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|privilege
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Privilege
name|aggregate
init|=
name|manager
operator|.
name|getPrivilege
argument_list|(
literal|"test:aggregate"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|aggregate
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|aggregate
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|aggregate
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|aggregate
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyCustomNodeTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeTypeManager
name|manager
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|manager
operator|.
name|hasNodeType
argument_list|(
literal|"test:unstructured"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeType
name|type
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
literal|"test:unstructured"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|type
operator|.
name|isMixin
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|type
operator|.
name|isNodeType
argument_list|(
literal|"nt:unstructured"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyNewBuiltinNodeTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeTypeManager
name|manager
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|manager
operator|.
name|hasNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_MEMBER_REFERENCES
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|manager
operator|.
name|hasNodeType
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyReplacedBuiltinNodeTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeTypeManager
name|manager
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|NodeType
name|nt
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_GROUP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Migrated repository must have new nodetype definitions"
argument_list|,
name|nt
operator|.
name|isNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_MEMBER_REFERENCES
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyGenericProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|session
operator|.
name|nodeExists
argument_list|(
literal|"/properties"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|properties
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/properties"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|BOOLEAN
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"boolean"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"boolean"
argument_list|)
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"binary"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Binary
name|binary
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"binary"
argument_list|)
operator|.
name|getBinary
argument_list|()
decl_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|binary
operator|.
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|byte
name|aBINARY
range|:
name|BINARY
control|)
block|{
name|assertEquals
argument_list|(
name|aBINARY
argument_list|,
operator|(
name|byte
operator|)
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|binary
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE
operator|.
name|getTimeInMillis
argument_list|()
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
operator|.
name|getDate
argument_list|()
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|DECIMAL
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"decimal"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|123
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"decimal"
argument_list|)
operator|.
name|getDecimal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|DOUBLE
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"double"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"double"
argument_list|)
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|LONG
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"long"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9876543210L
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"long"
argument_list|)
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"string"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"string"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"multiple"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Value
index|[]
name|values
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"multiple"
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|values
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|values
index|[
literal|1
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|values
index|[
literal|2
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyReferencePropertiesContent
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|session
operator|.
name|nodeExists
argument_list|(
literal|"/properties"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|properties
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/properties"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|REFERENCE
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"reference"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testNodeIdentifier
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"reference"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/referenceable"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"reference"
argument_list|)
operator|.
name|getNode
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyIterator
name|refs
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/referenceable"
argument_list|)
operator|.
name|getReferences
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|refs
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|properties
operator|.
name|getPath
argument_list|()
operator|+
literal|"/reference"
argument_list|,
name|refs
operator|.
name|nextProperty
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|refs
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyIterator
name|refs2
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/referenceable2"
argument_list|)
operator|.
name|getReferences
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|refs2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|properties
operator|.
name|getPath
argument_list|()
operator|+
literal|"/mv_reference"
argument_list|,
name|refs2
operator|.
name|nextProperty
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|refs2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"weak_reference"
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testNodeIdentifier
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"weak_reference"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/referenceable"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"weak_reference"
argument_list|)
operator|.
name|getNode
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyIterator
name|weakRefs
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/referenceable"
argument_list|)
operator|.
name|getWeakReferences
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|weakRefs
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|properties
operator|.
name|getPath
argument_list|()
operator|+
literal|"/weak_reference"
argument_list|,
name|weakRefs
operator|.
name|nextProperty
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|weakRefs
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyIterator
name|weakRefs2
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/referenceable2"
argument_list|)
operator|.
name|getWeakReferences
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|weakRefs2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|properties
operator|.
name|getPath
argument_list|()
operator|+
literal|"/mv_weak_reference"
argument_list|,
name|weakRefs2
operator|.
name|nextProperty
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|weakRefs2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

