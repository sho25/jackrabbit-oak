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
name|plugins
operator|.
name|observation
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
operator|.
name|toStringHelper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
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
name|NoSuchNodeTypeException
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
name|core
operator|.
name|ReadOnlyTree
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
name|plugins
operator|.
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
class|class
name|EventFilter
block|{
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|int
name|eventTypes
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|deep
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|uuids
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|nodeTypeOakName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|noLocal
decl_stmt|;
specifier|public
name|EventFilter
parameter_list|(
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|int
name|eventTypes
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|deep
parameter_list|,
name|String
index|[]
name|uuids
parameter_list|,
name|String
index|[]
name|nodeTypeName
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|NoSuchNodeTypeException
throws|,
name|RepositoryException
block|{
name|this
operator|.
name|ntMgr
operator|=
name|ntMgr
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|eventTypes
operator|=
name|eventTypes
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|deep
operator|=
name|deep
expr_stmt|;
name|this
operator|.
name|uuids
operator|=
name|uuids
expr_stmt|;
name|this
operator|.
name|nodeTypeOakName
operator|=
name|validateNodeTypeNames
argument_list|(
name|nodeTypeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|noLocal
operator|=
name|noLocal
expr_stmt|;
block|}
specifier|public
name|boolean
name|include
parameter_list|(
name|int
name|eventType
parameter_list|,
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|NodeState
name|associatedParentNode
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|eventType
argument_list|)
operator|&&
name|include
argument_list|(
name|path
argument_list|)
operator|&&
operator|(
name|associatedParentNode
operator|==
literal|null
operator|||
name|includeByType
argument_list|(
operator|new
name|ReadOnlyTree
argument_list|(
name|associatedParentNode
argument_list|)
argument_list|)
operator|)
operator|&&
operator|(
name|associatedParentNode
operator|==
literal|null
operator|||
name|includeByUuid
argument_list|(
name|associatedParentNode
argument_list|)
operator|)
return|;
block|}
specifier|public
name|boolean
name|includeChildren
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|this
operator|.
name|path
argument_list|)
operator|||
name|path
operator|.
name|equals
argument_list|(
name|this
operator|.
name|path
argument_list|)
operator|||
name|deep
operator|&&
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|excludeLocal
parameter_list|()
block|{
return|return
name|noLocal
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"types"
argument_list|,
name|eventTypes
argument_list|)
operator|.
name|add
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
operator|.
name|add
argument_list|(
literal|"deep"
argument_list|,
name|deep
argument_list|)
operator|.
name|add
argument_list|(
literal|"uuids"
argument_list|,
name|uuids
argument_list|)
operator|.
name|add
argument_list|(
literal|"node types"
argument_list|,
name|nodeTypeOakName
argument_list|)
operator|.
name|add
argument_list|(
literal|"noLocal"
argument_list|,
name|noLocal
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|//-----------------------------< internal>---------------------------------
specifier|private
name|boolean
name|include
parameter_list|(
name|int
name|eventType
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|eventTypes
operator|&
name|eventType
operator|)
operator|!=
literal|0
return|;
block|}
specifier|private
name|boolean
name|include
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|boolean
name|equalPaths
init|=
name|this
operator|.
name|path
operator|.
name|equals
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deep
operator|&&
operator|!
name|equalPaths
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|deep
operator|&&
operator|!
operator|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|this
operator|.
name|path
argument_list|,
name|path
argument_list|)
operator|||
name|equalPaths
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Checks whether to include an event based on the type of the associated      * parent node and the node type filter.      *      * @param associatedParentNode the associated parent node of the event.      * @return whether to include the event based on the type of the associated      *         parent node.      */
specifier|private
name|boolean
name|includeByType
parameter_list|(
name|Tree
name|associatedParentNode
parameter_list|)
block|{
if|if
condition|(
name|nodeTypeOakName
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
for|for
control|(
name|String
name|oakName
range|:
name|nodeTypeOakName
control|)
block|{
if|if
condition|(
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|associatedParentNode
argument_list|,
name|oakName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// filter has node types set but none matched
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|boolean
name|includeByUuid
parameter_list|(
name|NodeState
name|associatedParentNode
parameter_list|)
block|{
if|if
condition|(
name|uuids
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|uuids
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PropertyState
name|uuidProperty
init|=
name|associatedParentNode
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuidProperty
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|parentUuid
init|=
name|uuidProperty
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|uuid
range|:
name|uuids
control|)
block|{
if|if
condition|(
name|parentUuid
operator|.
name|equals
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Validates the given node type names.      *      * @param nodeTypeNames the node type names.      * @return the node type names as oak names.      * @throws javax.jcr.nodetype.NoSuchNodeTypeException if one of the node type names refers to      *                                 an non-existing node type.      * @throws javax.jcr.RepositoryException     if an error occurs while reading from the      *                                 node type manager.      */
annotation|@
name|CheckForNull
specifier|private
name|String
index|[]
name|validateNodeTypeNames
parameter_list|(
annotation|@
name|Nullable
name|String
index|[]
name|nodeTypeNames
parameter_list|)
throws|throws
name|NoSuchNodeTypeException
throws|,
name|RepositoryException
block|{
if|if
condition|(
name|nodeTypeNames
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|oakNames
init|=
operator|new
name|String
index|[
name|nodeTypeNames
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
name|nodeTypeNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ntMgr
operator|.
name|getNodeType
argument_list|(
name|nodeTypeNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|oakNames
index|[
name|i
index|]
operator|=
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|nodeTypeNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|oakNames
return|;
block|}
block|}
end_class

end_unit

