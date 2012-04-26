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
name|io
operator|.
name|IOException
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|InvalidNodeTypeDefinitionException
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
name|NodeTypeExistsException
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
specifier|static
specifier|final
name|Pattern
name|CND_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\[(.*)?\\](.*?\n)\n"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeType
argument_list|>
name|types
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
name|NameMapper
name|mapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|NodeTypeManagerImpl
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"builtin_nodetypes.cnd"
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|cnd
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|CND_PATTERN
operator|.
name|matcher
argument_list|(
name|cnd
argument_list|)
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|types
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|NodeTypeImpl
argument_list|(
name|this
argument_list|,
name|mapper
argument_list|,
name|name
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to load built-in node types"
argument_list|,
name|e
argument_list|)
throw|;
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
return|return
name|types
operator|.
name|containsKey
argument_list|(
name|mapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
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
name|NoSuchNodeTypeException
throws|,
name|RepositoryException
block|{
name|NodeType
name|type
init|=
name|types
operator|.
name|get
argument_list|(
name|mapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
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
return|return
operator|new
name|NodeTypeIteratorAdapter
argument_list|(
name|types
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
name|types
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
name|types
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
name|UnsupportedRepositoryOperationException
throws|,
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
name|NodeTypeTemplate
name|createNodeTypeTemplate
parameter_list|(
name|NodeTypeDefinition
name|ntd
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
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
name|NodeDefinitionTemplate
name|createNodeDefinitionTemplate
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
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
name|PropertyDefinitionTemplate
name|createPropertyDefinitionTemplate
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
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
name|InvalidNodeTypeDefinitionException
throws|,
name|NodeTypeExistsException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
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
name|InvalidNodeTypeDefinitionException
throws|,
name|NodeTypeExistsException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
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
name|unregisterNodeType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|NoSuchNodeTypeException
throws|,
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
name|UnsupportedRepositoryOperationException
throws|,
name|NoSuchNodeTypeException
throws|,
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

