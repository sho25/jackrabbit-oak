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
name|plugins
operator|.
name|nodetype
operator|.
name|write
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_SYSTEM
import|;
end_import

begin_import
import|import static
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
import|;
end_import

begin_import
import|import static
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|plugins
operator|.
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
import|;
end_import

begin_comment
comment|/**  * {@code ReadWriteNodeTypeManager} extends the {@link ReadOnlyNodeTypeManager}  * with support for operations that modify node types.  *<ul>  *<li>{@link #registerNodeType(NodeTypeDefinition, boolean)}</li>  *<li>{@link #registerNodeTypes(NodeTypeDefinition[], boolean)}</li>  *<li>{@link #unregisterNodeType(String)}</li>  *<li>{@link #unregisterNodeTypes(String[])}</li>  *<li>plus related template factory methods</li>  *</ul>  * Calling any of the above methods will result in a {@link #refresh()} callback  * to e.g. inform an associated session that it should refresh to make the  * changes visible.  *<p>  * Subclass responsibility is to provide an implementation of  * {@link #getTypes()} for read only access to the tree where node types are  * stored in content and {@link #getWriteRoot()} for write access to the  * repository in order to modify node types stored in content. A subclass may  * also want to override the default implementation of  * {@link ReadOnlyNodeTypeManager} for the following methods:  *<ul>  *<li>{@link #getValueFactory()}</li>  *<li>{@link ReadOnlyNodeTypeManager#getNamePathMapper()}</li>  *</ul>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReadWriteNodeTypeManager
extends|extends
name|ReadOnlyNodeTypeManager
block|{
comment|/**      * Called by the methods {@link #registerNodeType(NodeTypeDefinition, boolean)},      * {@link #registerNodeTypes(NodeTypeDefinition[], boolean)},      * {@link #unregisterNodeType(String)} and {@link #unregisterNodeTypes(String[])}      * to acquire a fresh {@link Root} instance that can be used to persist the      * requested node type changes (and nothing else).      *<p>      * This default implementation throws an {@link UnsupportedOperationException}.      *      * @return fresh {@link Root} instance.      */
annotation|@
name|Nonnull
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Called by the {@link ReadWriteNodeTypeManager} implementation methods to      * refresh the state of the session associated with this instance.      * That way the session is kept in sync with the latest global state      * seen by the node type manager.      *      * @throws RepositoryException if the session could not be refreshed      */
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{     }
comment|//----------------------------------------------------< NodeTypeManager>---
annotation|@
name|Override
specifier|public
name|NodeTypeTemplate
name|createNodeTypeTemplate
parameter_list|()
block|{
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|(
name|getNamePathMapper
argument_list|()
argument_list|)
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
name|ConstraintViolationException
block|{
return|return
operator|new
name|NodeTypeTemplateImpl
argument_list|(
name|getNamePathMapper
argument_list|()
argument_list|,
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
argument_list|(
name|getNamePathMapper
argument_list|()
argument_list|)
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
argument_list|(
name|getNamePathMapper
argument_list|()
argument_list|)
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
return|return
name|registerNodeTypes
argument_list|(
operator|new
name|NodeTypeDefinition
index|[]
block|{
name|ntd
block|}
argument_list|,
name|allowUpdate
argument_list|)
operator|.
name|nextNodeType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
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
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|Tree
name|tree
init|=
name|getOrCreateNodeTypes
argument_list|(
name|root
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeTypeDefinition
name|ntd
range|:
name|ntds
control|)
block|{
name|NodeTypeTemplateImpl
name|template
decl_stmt|;
if|if
condition|(
name|ntd
operator|instanceof
name|NodeTypeTemplateImpl
condition|)
block|{
name|template
operator|=
operator|(
name|NodeTypeTemplateImpl
operator|)
name|ntd
expr_stmt|;
block|}
else|else
block|{
comment|// some external template implementation, copy before proceeding
name|template
operator|=
operator|new
name|NodeTypeTemplateImpl
argument_list|(
name|getNamePathMapper
argument_list|()
argument_list|,
name|ntd
argument_list|)
expr_stmt|;
block|}
name|template
operator|.
name|writeTo
argument_list|(
name|tree
argument_list|,
name|allowUpdate
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NodeType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeType
argument_list|>
argument_list|(
name|ntds
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeTypeDefinition
name|ntd
range|:
name|ntds
control|)
block|{
name|types
operator|.
name|add
argument_list|(
name|getNodeType
argument_list|(
name|ntd
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|types
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to register node types."
decl_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Tree
name|getOrCreateNodeTypes
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|types
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Tree
name|system
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|'/'
operator|+
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|system
operator|.
name|exists
argument_list|()
condition|)
block|{
name|system
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
name|JCR_SYSTEM
argument_list|)
expr_stmt|;
block|}
name|types
operator|=
name|system
operator|.
name|addChild
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
expr_stmt|;
block|}
return|return
name|types
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
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|type
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
operator|.
name|getChild
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node type "
operator|+
name|name
operator|+
literal|" can not be unregistered."
argument_list|)
throw|;
block|}
try|try
block|{
name|type
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to unregister node type "
operator|+
name|name
decl_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|(
name|message
argument_list|)
throw|;
block|}
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
name|Root
name|root
init|=
name|getWriteRoot
argument_list|()
decl_stmt|;
name|Tree
name|types
init|=
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|types
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node types can not be unregistered."
argument_list|)
throw|;
block|}
try|try
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|Tree
name|type
init|=
name|types
operator|.
name|getChild
argument_list|(
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchNodeTypeException
argument_list|(
literal|"Node type "
operator|+
name|name
operator|+
literal|" can not be unregistered."
argument_list|)
throw|;
block|}
name|type
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to unregister node types."
decl_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

