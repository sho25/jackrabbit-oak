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
name|nodetype
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
name|Arrays
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
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
name|NoSuchNodeTypeException
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
name|NodeDefinitionTemplate
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
name|NodeTypeIterator
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
name|commons
operator|.
name|iterator
operator|.
name|NodeTypeIteratorAdapter
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
name|CommitFailedException
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
name|ContentSession
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
name|core
operator|.
name|DefaultConflictHandler
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
name|SessionDelegate
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
name|value
operator|.
name|ValueFactoryImpl
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
name|NameMapper
import|;
end_import

begin_class
specifier|public
class|class
name|NodeTypeManagerImpl
implements|implements
name|NodeTypeManager
block|{
specifier|private
specifier|final
name|ContentSession
name|cs
decl_stmt|;
specifier|private
specifier|final
name|ValueFactoryImpl
name|vf
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|NodeTypeManagerDelegate
name|ntmd
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeType
argument_list|>
name|typemap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|NodeTypeManagerImpl
parameter_list|(
name|SessionDelegate
name|sd
parameter_list|,
name|NodeTypeManagerDelegate
name|ntmd
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|cs
operator|=
name|sd
operator|.
name|getContentSession
argument_list|()
expr_stmt|;
name|this
operator|.
name|vf
operator|=
name|sd
operator|.
name|getValueFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|sd
operator|.
name|getNamePathMapper
argument_list|()
expr_stmt|;
name|this
operator|.
name|ntmd
operator|=
name|ntmd
expr_stmt|;
block|}
specifier|private
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|typemap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|NodeTypeDelegate
argument_list|>
name|alltypes
init|=
name|ntmd
operator|.
name|getAllNodeTypeDelegates
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeTypeDelegate
name|t
range|:
name|alltypes
control|)
block|{
name|NodeType
name|nt
init|=
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|vf
argument_list|,
name|mapper
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|typemap
operator|.
name|put
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|nt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|init
argument_list|()
expr_stmt|;
name|String
name|oakName
init|=
name|mapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// can be null, which is fine
return|return
name|typemap
operator|.
name|containsKey
argument_list|(
name|oakName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
name|getNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|init
argument_list|()
expr_stmt|;
name|String
name|oakName
init|=
name|mapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// can be null, which is fine
name|NodeType
name|type
init|=
name|typemap
operator|.
name|get
argument_list|(
name|oakName
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Unknown node type: "
operator|+
name|name
argument_list|)
throw|;
block|}
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getAllNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|init
argument_list|()
expr_stmt|;
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|typemap
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getPrimaryNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|init
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|primary
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|type
range|:
name|typemap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|isMixin
argument_list|()
condition|)
block|{
name|primary
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|primary
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|getMixinNodeTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|init
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|NodeType
argument_list|>
name|mixin
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|type
range|:
name|typemap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|isMixin
argument_list|()
condition|)
block|{
name|mixin
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|mixin
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeTemplate
name|createNodeTypeTemplate
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeTemplate
name|createNodeTypeTemplate
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|(
name|ntd
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDefinitionTemplate
name|createNodeDefinitionTemplate
parameter_list|()
block|{
return|return
operator|new
name|NodeDefinitionTemplateImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyDefinitionTemplate
name|createPropertyDefinitionTemplate
parameter_list|()
block|{
return|return
operator|new
name|PropertyDefinitionTemplateImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
name|registerNodeType
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO proper node type registration... (OAK-66)
try|try
block|{
name|Root
name|root
init|=
name|cs
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|NodeType
name|type
init|=
name|internalRegister
argument_list|(
name|ntd
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeIterator
name|registerNodeTypes
parameter_list|(
name|NodeTypeDefinition
index|[]
name|ntds
parameter_list|,
name|boolean
name|allowUpdate
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO handle inter-type dependencies (OAK-66)
try|try
block|{
name|Root
name|root
init|=
name|cs
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
name|NodeType
index|[]
name|types
init|=
operator|new
name|NodeType
index|[
name|ntds
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ntds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
name|internalRegister
argument_list|(
name|ntds
index|[
name|i
index|]
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|types
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|NodeType
name|internalRegister
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|,
name|Tree
name|root
parameter_list|)
block|{
name|NodeTypeDelegate
name|delegate
init|=
operator|new
name|NodeTypeDelegate
argument_list|(
name|ntd
operator|.
name|getName
argument_list|()
argument_list|,
name|ntd
operator|.
name|getDeclaredSupertypeNames
argument_list|()
argument_list|,
name|ntd
operator|.
name|getPrimaryItemName
argument_list|()
argument_list|,
name|ntd
operator|.
name|isMixin
argument_list|()
argument_list|,
name|ntd
operator|.
name|isAbstract
argument_list|()
argument_list|,
name|ntd
operator|.
name|hasOrderableChildNodes
argument_list|()
argument_list|)
decl_stmt|;
name|NodeType
name|type
init|=
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|vf
argument_list|,
name|mapper
argument_list|,
name|delegate
argument_list|)
decl_stmt|;
name|typemap
operator|.
name|put
argument_list|(
name|ntd
operator|.
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|Tree
name|system
init|=
name|root
operator|.
name|getChild
argument_list|(
literal|"jcr:system"
argument_list|)
decl_stmt|;
if|if
condition|(
name|system
operator|==
literal|null
condition|)
block|{
name|system
operator|=
name|root
operator|.
name|addChild
argument_list|(
literal|"jcr:system"
argument_list|)
expr_stmt|;
block|}
name|Tree
name|nodetypes
init|=
name|system
operator|.
name|getChild
argument_list|(
literal|"jcr:nodeTypes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodetypes
operator|==
literal|null
condition|)
block|{
name|nodetypes
operator|=
name|system
operator|.
name|addChild
argument_list|(
literal|"jcr:nodeTypes"
argument_list|)
expr_stmt|;
block|}
name|nodetypes
operator|.
name|addChild
argument_list|(
name|ntd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregisterNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregisterNodeTypes
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

