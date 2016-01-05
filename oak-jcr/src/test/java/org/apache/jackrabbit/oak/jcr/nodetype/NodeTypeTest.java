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
name|jcr
operator|.
name|nodetype
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|PropertyType
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
name|ValueFactory
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
name|ConstraintViolationException
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
name|NodeTypeDefinition
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
name|nodetype
operator|.
name|PropertyDefinitionTemplate
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|jcr
operator|.
name|AbstractRepositoryTest
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

begin_class
specifier|public
class|class
name|NodeTypeTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|NodeTypeTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a node to a node type that does not accept child nodes      * See OAK-479      */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|illegalAddNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"q2"
argument_list|,
literal|"nt:query"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|illegalAddNodeWithProps
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|n
operator|.
name|addNode
argument_list|(
literal|"q2"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|n2
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateNodeType
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
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
name|Node
name|n
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|NodeTypeDefinition
name|ntd
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
literal|"nt:query"
argument_list|)
decl_stmt|;
name|NodeTypeTemplate
name|ntt
init|=
name|manager
operator|.
name|createNodeTypeTemplate
argument_list|(
name|ntd
argument_list|)
decl_stmt|;
try|try
block|{
name|manager
operator|.
name|registerNodeType
argument_list|(
name|ntt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// no changes to the type, so the registration should be a no-op
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|unexpected
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
comment|// make the (still missing) jcr:language property mandatory
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|PropertyDefinitionTemplate
argument_list|>
name|pdts
init|=
name|ntt
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyDefinitionTemplate
name|pdt
range|:
name|pdts
control|)
block|{
if|if
condition|(
literal|"jcr:language"
operator|.
name|equals
argument_list|(
name|pdt
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|pdt
operator|.
name|setMandatory
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|manager
operator|.
name|registerNodeType
argument_list|(
name|ntt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|expected
parameter_list|)
block|{
comment|// the registration fails because of the would-be invalid content
block|}
comment|// add the jcr:language property so it can be made mandatory
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|manager
operator|.
name|registerNodeType
argument_list|(
name|ntt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// now the mandatory property exists, so the type change is OK
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|unexpected
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|trivialUpdates
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test various trivial updates that should not trigger repository scans
comment|// whether or not the repository scan happens can not be checked directly;
comment|// it requires inspecting the INFO level log
name|String
index|[]
name|types
init|=
operator|new
name|String
index|[]
block|{
literal|"trivial1"
block|,
literal|"trivial2"
block|}
decl_stmt|;
name|ArrayList
argument_list|<
name|NodeTypeTemplate
argument_list|>
name|ntt
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeTypeTemplate
argument_list|>
argument_list|()
decl_stmt|;
comment|// adding node types
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
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
for|for
control|(
name|String
name|t
range|:
name|types
control|)
block|{
name|NodeTypeTemplate
name|nt
init|=
name|manager
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|nt
operator|.
name|setName
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|ntt
operator|.
name|add
argument_list|(
name|nt
argument_list|)
expr_stmt|;
block|}
name|manager
operator|.
name|registerNodeTypes
argument_list|(
name|ntt
operator|.
name|toArray
argument_list|(
operator|new
name|NodeTypeTemplate
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// adding an optional property
name|ntt
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeTypeTemplate
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|t
range|:
name|types
control|)
block|{
name|NodeTypeDefinition
name|ntd
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|PropertyDefinitionTemplate
name|opt
init|=
name|manager
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|opt
operator|.
name|setMandatory
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|opt
operator|.
name|setName
argument_list|(
literal|"optional"
argument_list|)
expr_stmt|;
name|opt
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|PropertyDefinitionTemplate
name|opts
init|=
name|manager
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|opts
operator|.
name|setMandatory
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setMultiple
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setName
argument_list|(
literal|"optionals"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|NodeTypeTemplate
name|nt
init|=
name|manager
operator|.
name|createNodeTypeTemplate
argument_list|(
name|ntd
argument_list|)
decl_stmt|;
name|List
name|pdt
init|=
name|nt
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
decl_stmt|;
name|pdt
operator|.
name|add
argument_list|(
name|opt
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|add
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|ntt
operator|.
name|add
argument_list|(
name|nt
argument_list|)
expr_stmt|;
block|}
name|manager
operator|.
name|registerNodeTypes
argument_list|(
name|ntt
operator|.
name|toArray
argument_list|(
operator|new
name|NodeTypeTemplate
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// make one optional property mandatory
name|ntt
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeTypeTemplate
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|t
range|:
name|types
control|)
block|{
name|NodeTypeDefinition
name|ntd
init|=
name|manager
operator|.
name|getNodeType
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|PropertyDefinitionTemplate
name|opt
init|=
name|manager
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|opt
operator|.
name|setMandatory
argument_list|(
literal|"trivial2"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|opt
operator|.
name|setName
argument_list|(
literal|"optional"
argument_list|)
expr_stmt|;
name|opt
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|PropertyDefinitionTemplate
name|opts
init|=
name|manager
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|opts
operator|.
name|setMandatory
argument_list|(
literal|"trivial2"
operator|.
name|equals
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setMultiple
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setName
argument_list|(
literal|"optionals"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|NodeTypeTemplate
name|nt
init|=
name|manager
operator|.
name|createNodeTypeTemplate
argument_list|(
name|ntd
argument_list|)
decl_stmt|;
name|List
name|pdt
init|=
name|nt
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
decl_stmt|;
name|pdt
operator|.
name|add
argument_list|(
name|opt
argument_list|)
expr_stmt|;
name|pdt
operator|.
name|add
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|ntt
operator|.
name|add
argument_list|(
name|nt
argument_list|)
expr_stmt|;
block|}
comment|// but update both node types
name|manager
operator|.
name|registerNodeTypes
argument_list|(
name|ntt
operator|.
name|toArray
argument_list|(
operator|new
name|NodeTypeTemplate
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeType
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
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
name|Node
name|n
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"q1"
argument_list|,
literal|"nt:query"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:statement"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"statement"
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:language"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"language"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|manager
operator|.
name|unregisterNodeType
argument_list|(
literal|"nt:query"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|expected
parameter_list|)
block|{
comment|// this type is referenced in content, so it can't be removed
block|}
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|manager
operator|.
name|unregisterNodeType
argument_list|(
literal|"nt:query"
argument_list|)
expr_stmt|;
comment|// no longer referenced in content, so removal should succeed
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|unexpected
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|mixReferenceable
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|a
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"a"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// No problem here
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|Node
name|b
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"b"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|b
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// fails as jcr:uuid is protected
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|expected
parameter_list|)
block|{ }
name|Node
name|c
init|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"c"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Doesn't fail as jcr:uuid is not protected yet
name|c
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

