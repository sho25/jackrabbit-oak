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
name|security
operator|.
name|user
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
name|Iterator
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
name|PropertyIterator
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * JCR level implementation of the internal {@code AuthorizableProperties}  * interface. It will be used whenever a {@code Session} is associated  * with a user manager and thus a JCR item operations can be used to retrieve  * the authorizable properties.  */
end_comment

begin_class
class|class
name|JcrAuthorizableProperties
implements|implements
name|AuthorizableProperties
implements|,
name|UserConstants
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JcrAuthorizableProperties
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Node
name|authorizableNode
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
name|JcrAuthorizableProperties
parameter_list|(
name|Node
name|authorizableNode
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|authorizableNode
operator|=
name|authorizableNode
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
comment|//---------------------------------------------< AuthorizableProperties>---
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|getNode
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|node
operator|.
name|getNode
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyIterator
name|it
init|=
name|n
operator|.
name|getProperties
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Property
name|prop
init|=
name|it
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|prop
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Relative path "
operator|+
name|relPath
operator|+
literal|" refers to items outside of scope of authorizable."
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#hasProperty(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|getNode
argument_list|()
decl_stmt|;
return|return
name|node
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
operator|&&
name|isAuthorizableProperty
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#getProperty(String)      */
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|getNode
argument_list|()
decl_stmt|;
name|Value
index|[]
name|values
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
name|Property
name|prop
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|prop
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|prop
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|values
operator|=
name|prop
operator|.
name|getValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
operator|new
name|Value
index|[]
block|{
name|prop
operator|.
name|getValue
argument_list|()
block|}
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#setProperty(String, javax.jcr.Value)      */
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|relPath
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|String
name|intermediate
init|=
operator|(
name|relPath
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|?
literal|null
else|:
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|relPath
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|getOrCreateTargetNode
argument_list|(
name|intermediate
argument_list|)
decl_stmt|;
comment|// check if the property has already been created as multi valued
comment|// property before -> in this case remove in order to avoid
comment|// ValueFormatException.
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|p
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|n
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#setProperty(String, javax.jcr.Value[])      */
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|relPath
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|String
name|intermediate
init|=
operator|(
name|relPath
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|?
literal|null
else|:
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|relPath
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|getOrCreateTargetNode
argument_list|(
name|intermediate
argument_list|)
decl_stmt|;
comment|// check if the property has already been created as single valued
comment|// property before -> in this case remove in order to avoid
comment|// ValueFormatException.
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|p
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|n
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#removeProperty(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|removeProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|p
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Property "
operator|+
name|relPath
operator|+
literal|" isn't a modifiable authorizable property"
argument_list|)
throw|;
block|}
block|}
comment|// no such property or wasn't a property of this authorizable.
return|return
literal|false
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Node
name|getNode
parameter_list|()
block|{
return|return
name|authorizableNode
return|;
block|}
specifier|private
name|String
name|getJcrName
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
return|;
block|}
comment|/**      * Returns true if the given property of the authorizable node is one of the      * non-protected properties defined by the rep:Authorizable node type or a      * some other descendant of the authorizable node.      *      * @param prop Property to be tested.      * @param verifyAncestor If true the property is tested to be a descendant      * of the node of this authorizable; otherwise it is expected that this      * test has been executed by the caller.      * @return {@code true} if the given property is defined      * by the rep:authorizable node type or one of it's sub-node types;      * {@code false} otherwise.      * @throws RepositoryException If the property definition cannot be retrieved.      */
specifier|private
name|boolean
name|isAuthorizableProperty
parameter_list|(
name|Property
name|prop
parameter_list|,
name|boolean
name|verifyAncestor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|verifyAncestor
operator|&&
operator|!
name|Text
operator|.
name|isDescendant
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|prop
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Attempt to access property outside of authorizable scope."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|PropertyDefinition
name|def
init|=
name|prop
operator|.
name|getDefinition
argument_list|()
decl_stmt|;
if|if
condition|(
name|def
operator|.
name|isProtected
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isSame
argument_list|(
name|prop
operator|.
name|getParent
argument_list|()
argument_list|)
condition|)
block|{
name|NodeType
name|declaringNt
init|=
name|prop
operator|.
name|getDefinition
argument_list|()
operator|.
name|getDeclaringNodeType
argument_list|()
decl_stmt|;
return|return
name|declaringNt
operator|.
name|isNodeType
argument_list|(
name|getJcrName
argument_list|(
name|NT_REP_AUTHORIZABLE
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// another non-protected property somewhere in the subtree of this
comment|// authorizable node -> is a property that can be set using #setProperty.
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Retrieves the node at {@code relPath} relative to node associated with      * this authorizable. If no such node exist it and any missing intermediate      * nodes are created.      *      * @param relPath A relative path.      * @return The corresponding node.      * @throws RepositoryException If an error occurs or if {@code relPath} refers      * to a node that is outside of the scope of this authorizable.      */
annotation|@
name|Nonnull
specifier|private
name|Node
name|getOrCreateTargetNode
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|n
decl_stmt|;
name|Node
name|node
init|=
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|relPath
operator|!=
literal|null
condition|)
block|{
name|String
name|userPath
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
name|n
operator|=
name|node
operator|.
name|getNode
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|userPath
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Relative path "
operator|+
name|relPath
operator|+
literal|" outside of scope of "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|n
operator|=
name|node
expr_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|Text
operator|.
name|explode
argument_list|(
name|relPath
argument_list|,
literal|'/'
argument_list|)
control|)
block|{
if|if
condition|(
name|n
operator|.
name|hasNode
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|getNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|userPath
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Relative path "
operator|+
name|relPath
operator|+
literal|" outside of scope of "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
name|n
operator|=
name|node
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
block|}
end_class

end_unit

