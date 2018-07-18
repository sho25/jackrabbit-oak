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
name|benchmark
operator|.
name|util
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
name|base
operator|.
name|Strings
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
name|commons
operator|.
name|JcrUtils
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
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
name|property
operator|.
name|OrderedIndex
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|Property
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
name|RepositoryException
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

begin_comment
comment|/**  * A simple utility class for Oak indexes.  */
end_comment

begin_class
specifier|public
class|class
name|OakIndexUtils
block|{
comment|/**      * A property index      */
specifier|public
specifier|static
class|class
name|PropertyIndex
block|{
specifier|private
name|String
name|indexName
decl_stmt|;
specifier|private
name|String
name|propertyName
decl_stmt|;
specifier|private
name|String
index|[]
name|nodeTypeNames
decl_stmt|;
comment|/**          * Set the index name. If not set, the index name is the property name.          *           * @param indexName the index name          * @return this          */
specifier|public
name|PropertyIndex
name|name
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Set the property name. This field is mandatory.          *           * @param propertyName the property name          * @return this          */
specifier|public
name|PropertyIndex
name|property
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Restrict the node types.          *           * @param nodeTypeNames the list of declaring node types          * @return this          */
specifier|public
name|PropertyIndex
name|nodeTypes
parameter_list|(
name|String
modifier|...
name|nodeTypeNames
parameter_list|)
block|{
name|this
operator|.
name|nodeTypeNames
operator|=
name|nodeTypeNames
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create the index.          *<p>          * If this is not a Oak repository, this method does nothing.          *<p>          * If a matching index already exists, this method verifies that the          * definition matches. If no such index exists, a new one is created.          *           * @param session the session to use for creating the index          * @return the index node          * @throws RepositoryException if writing to the repository failed, the          *             index definition is incorrect, or if such an index exists          *             but is not compatible with this definition (for example,          *             a different property is indexed)          */
specifier|public
annotation|@
name|Nullable
name|Node
name|create
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|create
argument_list|(
name|session
argument_list|,
name|PropertyIndexEditorProvider
operator|.
name|TYPE
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Node
name|create
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|indexType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|index
decl_stmt|;
if|if
condition|(
operator|!
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
operator|.
name|hasNodeType
argument_list|(
literal|"oak:QueryIndexDefinition"
argument_list|)
condition|)
block|{
comment|// not an Oak repository
return|return
literal|null
return|;
block|}
if|if
condition|(
name|session
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The session has pending changes"
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexName
operator|==
literal|null
condition|)
block|{
name|indexName
operator|=
name|propertyName
expr_stmt|;
block|}
if|if
condition|(
name|propertyName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index property name not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|nodeTypeNames
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nodeTypeNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// setting the node types to an empty array means "all node types"
comment|// (same as not setting it)
name|nodeTypeNames
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|nodeTypeNames
argument_list|)
expr_stmt|;
block|}
block|}
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|indexDef
decl_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|hasNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
condition|)
block|{
name|indexDef
operator|=
name|root
operator|.
name|addNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexDef
operator|=
name|root
operator|.
name|getNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexDef
operator|.
name|hasNode
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
comment|// verify the index matches
name|index
operator|=
name|indexDef
operator|.
name|getNode
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|.
name|hasProperty
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index already exists, but is unique"
argument_list|)
throw|;
block|}
block|}
name|String
name|type
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|indexType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index already exists, but is of type "
operator|+
name|type
argument_list|)
throw|;
block|}
name|Value
index|[]
name|v
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|String
index|[]
name|list
init|=
operator|new
name|String
index|[
name|v
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
name|v
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
name|v
index|[
name|i
index|]
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index already exists, but is not just one property, but "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|list
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|propertyName
operator|.
name|equals
argument_list|(
name|v
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index already exists, but is for property "
operator|+
name|v
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|.
name|hasProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|)
condition|)
block|{
name|v
operator|=
name|index
operator|.
name|getProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|)
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|String
index|[]
name|list
init|=
operator|new
name|String
index|[
name|v
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
name|v
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
name|v
index|[
name|i
index|]
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|list
argument_list|,
name|nodeTypeNames
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index already exists, but with different node types: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|list
argument_list|)
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|nodeTypeNames
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Index already exists, but without node type restriction"
argument_list|)
throw|;
block|}
comment|// matches
return|return
name|index
return|;
block|}
name|index
operator|=
name|indexDef
operator|.
name|addNode
argument_list|(
name|indexName
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|indexType
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|propertyName
block|}
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeTypeNames
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|nodeTypeNames
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|index
return|;
block|}
block|}
comment|/**      * Helper method to create or update a property index definition.      *      * @param session the session      * @param indexDefinitionName the name of the node for the index definition      * @param propertyNames the list of properties to index      * @param unique if unique or not      * @param enclosingNodeTypes the enclosing node types      * @return the node just created      * @throws RepositoryException the repository exception      */
specifier|public
specifier|static
name|Node
name|propertyIndexDefinition
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|indexDefinitionName
parameter_list|,
name|String
index|[]
name|propertyNames
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|String
index|[]
name|enclosingNodeTypes
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|indexDefRoot
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|root
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|indexDef
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|indexDefRoot
argument_list|,
name|indexDefinitionName
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|PropertyIndexEditorProvider
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|propertyNames
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|,
name|unique
argument_list|)
expr_stmt|;
if|if
condition|(
name|enclosingNodeTypes
operator|!=
literal|null
operator|&&
name|enclosingNodeTypes
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|enclosingNodeTypes
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|indexDef
return|;
block|}
comment|/**      * Helper method to create or update an ordered index definition.      *      * @param session the session      * @param indexDefinitionName the name of the node for the index definition      * @param async whether the indexing is async or not      * @param propertyNames the list of properties to index      * @param unique if unique or not      * @param enclosingNodeTypes the enclosing node types      * @param direction the direction      * @return the node just created      * @throws RepositoryException the repository exception      */
specifier|public
specifier|static
name|Node
name|orderedIndexDefinition
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|indexDefinitionName
parameter_list|,
name|String
name|async
parameter_list|,
name|String
index|[]
name|propertyNames
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|String
index|[]
name|enclosingNodeTypes
parameter_list|,
name|String
name|direction
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|indexDefRoot
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|root
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|indexDef
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|indexDefRoot
argument_list|,
name|indexDefinitionName
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|OrderedIndex
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|propertyNames
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|enclosingNodeTypes
operator|!=
literal|null
operator|&&
name|enclosingNodeTypes
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|enclosingNodeTypes
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|direction
operator|!=
literal|null
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|OrderedIndex
operator|.
name|DIRECTION
argument_list|,
name|direction
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|async
operator|!=
literal|null
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|,
name|unique
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|indexDef
return|;
block|}
comment|/**      * Helper method to create or update a lucene property index definition.      *      * @param session the session      * @param indexDefinitionName the name of the node for the index definition      * @param propertyNames the list of properties to index      * @param type the types of the properties in order of the properties      * @param orderedPropsMap the ordered props and its properties      * @param persistencePath the path if the persistence=file (default is repository)      * @return the node just created      * @throws RepositoryException the repository exception      */
specifier|public
specifier|static
name|Node
name|luceneIndexDefinition
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|indexDefinitionName
parameter_list|,
name|String
name|async
parameter_list|,
name|String
index|[]
name|propertyNames
parameter_list|,
name|String
index|[]
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|orderedPropsMap
parameter_list|,
name|String
name|persistencePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|indexDefRoot
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|root
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|indexDef
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|indexDefRoot
argument_list|,
name|indexDefinitionName
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|FULL_TEXT_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|async
operator|!=
literal|null
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
comment|// Set indexed property names
name|indexDef
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|propertyNames
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Node
name|propsNode
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|indexDef
argument_list|,
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
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
name|propertyNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|propNode
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|propsNode
argument_list|,
name|propertyNames
index|[
name|i
index|]
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|propNode
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|type
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Set ordered property names
if|if
condition|(
operator|(
name|orderedPropsMap
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|orderedPropsMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|orderedProps
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|orderedPropEntry
range|:
name|orderedPropsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Node
name|propNode
init|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|propsNode
argument_list|,
name|orderedPropEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|propNode
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|orderedPropEntry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|orderedProps
operator|.
name|add
argument_list|(
name|orderedPropEntry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|orderedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|ORDERED_PROP_NAMES
argument_list|,
name|orderedProps
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|orderedProps
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Set file persistence if specified
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|persistencePath
argument_list|)
condition|)
block|{
name|indexDef
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_FILE
argument_list|)
expr_stmt|;
name|indexDef
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_PATH
argument_list|,
name|persistencePath
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|indexDef
return|;
block|}
block|}
end_class

end_unit

