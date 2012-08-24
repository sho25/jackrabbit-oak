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
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
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
name|PropertyDefinition
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
name|CoreValue
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
name|Tree
operator|.
name|Status
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
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_comment
comment|/**  * {@code PropertyDelegate} serve as internal representations of {@code Property}s.  * Most methods of this class throw an {@code InvalidItemStateException}  * exception if the instance is stale. An instance is stale if the underlying  * items does not exist anymore.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyDelegate
extends|extends
name|ItemDelegate
block|{
comment|/** The underlying {@link TreeLocation} of this node. */
specifier|private
name|TreeLocation
name|location
decl_stmt|;
name|PropertyDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|propertyState
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
assert|assert
name|parent
operator|!=
literal|null
assert|;
assert|assert
name|propertyState
operator|!=
literal|null
assert|;
name|this
operator|.
name|location
operator|=
name|parent
operator|.
name|getLocation
argument_list|()
operator|.
name|getChild
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PropertyDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|TreeLocation
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
assert|assert
name|location
operator|!=
literal|null
assert|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|resolve
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|location
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Property is stale"
argument_list|)
throw|;
block|}
return|return
name|path
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getParent
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|getParentTree
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStale
parameter_list|()
block|{
name|resolve
argument_list|()
expr_stmt|;
return|return
name|location
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|REMOVED
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|resolve
argument_list|()
expr_stmt|;
name|Status
name|propertyStatus
init|=
name|location
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyStatus
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Property is stale"
argument_list|)
throw|;
block|}
return|return
name|propertyStatus
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// don't disturb the state: avoid resolving the tree
return|return
literal|"PropertyDelegate["
operator|+
name|location
operator|.
name|getPath
argument_list|()
operator|+
literal|']'
return|;
block|}
comment|/**      * Get the value of the property      * @return  the value of the property      * @throws IllegalStateException  if {@code isMultivalue()} is {@code true}.      *      */
annotation|@
name|Nonnull
specifier|public
name|CoreValue
name|getValue
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**      * Get the value of the property      * @return  the values of the property      * @throws IllegalStateException  if {@code isMultivalue()} is {@code false}.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|CoreValue
argument_list|>
name|getValues
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|getValues
argument_list|()
return|;
block|}
comment|/**      * Determine whether the property is multi valued      * @return  {@code true} if multi valued      */
specifier|public
name|boolean
name|isMultivalue
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|isArray
argument_list|()
return|;
block|}
comment|/**      * Get the property definition of the property      * @return      */
annotation|@
name|Nonnull
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|()
block|{
comment|// TODO
return|return
operator|new
name|PropertyDefinition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRequiredType
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getValueConstraints
parameter_list|()
block|{
comment|// TODO
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getDefaultValues
parameter_list|()
block|{
comment|// TODO
return|return
operator|new
name|Value
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiple
parameter_list|()
block|{
comment|// TODO
try|try
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|isArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
comment|// todo implement catch e
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAvailableQueryOperators
parameter_list|()
block|{
comment|// TODO
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFullTextSearchable
parameter_list|()
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isQueryOrderable
parameter_list|()
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeType
name|getDeclaringNodeType
parameter_list|()
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
comment|// TODO
try|try
block|{
return|return
name|getPropertyState
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// todo implement catch e
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAutoCreated
parameter_list|()
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMandatory
parameter_list|()
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOnParentVersion
parameter_list|()
block|{
comment|// TODO
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isProtected
parameter_list|()
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
comment|/**      * Set the value of the property      * @param value      */
specifier|public
name|void
name|setValue
parameter_list|(
name|CoreValue
name|value
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|getParentTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the values of the property      * @param values      */
specifier|public
name|void
name|setValues
parameter_list|(
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|getParentTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|getName
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove the property      */
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|getParentTree
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nonnull
specifier|private
name|PropertyState
name|getPropertyState
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|resolve
argument_list|()
expr_stmt|;
name|PropertyState
name|property
init|=
name|location
operator|.
name|getProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Property is stale"
argument_list|)
throw|;
block|}
return|return
name|property
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getParentTree
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|resolve
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|location
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Parent node is stale"
argument_list|)
throw|;
block|}
return|return
name|tree
return|;
block|}
specifier|synchronized
name|void
name|resolve
parameter_list|()
block|{
name|String
name|path
init|=
name|location
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|Tree
name|parent
init|=
name|sessionDelegate
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|location
operator|=
name|parent
operator|.
name|getLocation
argument_list|()
operator|.
name|getChild
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

