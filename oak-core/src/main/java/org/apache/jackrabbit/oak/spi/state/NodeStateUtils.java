begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|state
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
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

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
name|Strings
operator|.
name|repeat
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
name|Nonnull
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

begin_comment
comment|/**  * Utility method for code that deals with node states.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeStateUtils
block|{
specifier|private
name|NodeStateUtils
parameter_list|()
block|{     }
comment|/**      * Check whether the node or property with the given name is hidden, that      * is, if the node name starts with a ":".      *      * @param name the node or property name      * @return true if the item is hidden      */
specifier|public
specifier|static
name|boolean
name|isHidden
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
operator|&&
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|':'
return|;
block|}
comment|/**      * Check whether the given path contains a hidden node.      *       * @param path the path      * @return true if one of the nodes is hidden      */
specifier|public
specifier|static
name|boolean
name|isHiddenPath
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|n
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|isHidden
argument_list|(
name|n
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
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|String
name|getPrimaryTypeName
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|nodeState
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
return|return
operator|(
name|ps
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
return|;
block|}
comment|/**      * Get a possibly non existing child node of a node.      * @param node  node whose child node to get      * @param path  path of the child node      * @return  child node of {@code node} at {@code path}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|NodeState
name|getNode
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|node
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
comment|/**      * Provides a string representation of the given node state      *       * @param node      *            node state      * @return a string representation of {@code node}.      */
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|"[null]"
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|node
argument_list|,
literal|1
argument_list|,
literal|"  "
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|NodeState
name|ns
parameter_list|,
name|int
name|level
parameter_list|,
name|String
name|prepend
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|StringBuilder
name|node
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|node
operator|.
name|append
argument_list|(
name|repeat
argument_list|(
name|prepend
argument_list|,
name|level
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|StringBuilder
name|props
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|ns
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|props
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
name|props
operator|.
name|append
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|props
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|node
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|node
operator|.
name|append
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|node
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|c
range|:
name|ns
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|node
operator|.
name|append
argument_list|(
name|IOUtils
operator|.
name|LINE_SEPARATOR
argument_list|)
expr_stmt|;
name|node
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|c
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|level
operator|+
literal|1
argument_list|,
name|prepend
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|node
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

