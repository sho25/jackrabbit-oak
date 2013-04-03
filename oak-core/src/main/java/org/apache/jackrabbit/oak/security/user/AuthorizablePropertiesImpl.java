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
name|Arrays
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
name|memory
operator|.
name|PropertyStates
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
name|plugins
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
name|oak
operator|.
name|util
operator|.
name|NodeUtil
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
name|util
operator|.
name|TreeUtil
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
comment|/**  * Oak level implementation of the internal {@code AuthorizableProperties} that  * is used in those cases where no {@code Session} is associated with the  * {@code UserManager} and only OAK API methods can be used to read and  * modify authorizable properties.  */
end_comment

begin_class
class|class
name|AuthorizablePropertiesImpl
implements|implements
name|AuthorizableProperties
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
name|AuthorizablePropertiesImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AuthorizableImpl
name|authorizable
decl_stmt|;
name|AuthorizablePropertiesImpl
parameter_list|(
name|AuthorizableImpl
name|authorizable
parameter_list|)
block|{
name|this
operator|.
name|authorizable
operator|=
name|authorizable
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
name|checkRelativePath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
name|TreeLocation
name|location
init|=
name|getLocation
argument_list|(
name|tree
argument_list|,
name|relPath
argument_list|)
decl_stmt|;
name|Tree
name|parent
init|=
name|location
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|parent
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
name|PropertyState
name|property
range|:
name|parent
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|propName
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|tree
argument_list|,
name|location
operator|.
name|getChild
argument_list|(
name|propName
argument_list|)
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|propName
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
name|RepositoryException
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
name|checkRelativePath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
return|return
name|isAuthorizableProperty
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|getLocation
argument_list|(
name|getTree
argument_list|()
argument_list|,
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
name|checkRelativePath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
name|Value
index|[]
name|values
init|=
literal|null
decl_stmt|;
name|PropertyState
name|property
init|=
name|getAuthorizableProperty
argument_list|(
name|tree
argument_list|,
name|getLocation
argument_list|(
name|tree
argument_list|,
name|relPath
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|NamePathMapper
name|npMapper
init|=
name|authorizable
operator|.
name|getUserManager
argument_list|()
operator|.
name|getNamePathMapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
name|ValueFactoryImpl
operator|.
name|createValues
argument_list|(
name|property
argument_list|,
name|npMapper
argument_list|)
decl_stmt|;
name|values
operator|=
name|vs
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|vs
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|property
argument_list|,
name|npMapper
argument_list|)
block|}
expr_stmt|;
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
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|removeProperty
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkRelativePath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
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
name|PropertyState
name|propertyState
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
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
name|Tree
name|parent
init|=
name|getOrCreateTargetTree
argument_list|(
name|intermediate
argument_list|)
decl_stmt|;
name|checkProtectedProperty
argument_list|(
name|parent
argument_list|,
name|propertyState
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|removeProperty
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkRelativePath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
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
name|PropertyState
name|propertyState
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
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
name|Tree
name|parent
init|=
name|getOrCreateTargetTree
argument_list|(
name|intermediate
argument_list|)
decl_stmt|;
name|checkProtectedProperty
argument_list|(
name|parent
argument_list|,
name|propertyState
argument_list|)
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
block|}
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
name|checkRelativePath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
name|Tree
name|node
init|=
name|getTree
argument_list|()
decl_stmt|;
name|TreeLocation
name|propertyLocation
init|=
name|TreeUtil
operator|.
name|getTreeLocation
argument_list|(
name|node
argument_list|,
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyLocation
operator|.
name|getProperty
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|node
argument_list|,
name|propertyLocation
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|propertyLocation
operator|.
name|remove
argument_list|()
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
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|authorizable
operator|.
name|getTree
argument_list|()
return|;
block|}
comment|/**      * Returns true if the given property of the authorizable node is one of the      * non-protected properties defined by the rep:Authorizable node type or a      * some other descendant of the authorizable node.      *      * @param authorizableTree The tree of the target authorizable.      * @param propertyLocation Location to be tested.      * @param verifyAncestor   If true the property is tested to be a descendant      *                         of the node of this authorizable; otherwise it is expected that this      *                         test has been executed by the caller.      * @return {@code true} if the given property is not protected and is defined      *         by the rep:authorizable node type or one of it's sub-node types;      *         {@code false} otherwise.      * @throws RepositoryException If an error occurs.      */
specifier|private
name|boolean
name|isAuthorizableProperty
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|TreeLocation
name|propertyLocation
parameter_list|,
name|boolean
name|verifyAncestor
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|getAuthorizableProperty
argument_list|(
name|authorizableTree
argument_list|,
name|propertyLocation
argument_list|,
name|verifyAncestor
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the valid authorizable property identified by the specified      * property location or {@code null} if that property does not exist or      * isn't a authorizable property because it is protected or outside of the      * scope of the {@code authorizableTree}.      *      * @param authorizableTree The tree of the target authorizable.      * @param propertyLocation Location to be tested.      * @param verifyAncestor   If true the property is tested to be a descendant      *                         of the node of this authorizable; otherwise it is expected that this      *                         test has been executed by the caller.      * @return a valid authorizable property or {@code null} if no such property      *         exists or fi the property is protected or not defined by the rep:authorizable      *         node type or one of it's sub-node types.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|CheckForNull
specifier|private
name|PropertyState
name|getAuthorizableProperty
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|TreeLocation
name|propertyLocation
parameter_list|,
name|boolean
name|verifyAncestor
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|propertyLocation
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyState
name|property
init|=
name|propertyLocation
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
return|return
literal|null
return|;
block|}
name|String
name|authorizablePath
init|=
name|authorizableTree
operator|.
name|getPath
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
name|authorizablePath
argument_list|,
name|propertyLocation
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
literal|null
return|;
block|}
name|Tree
name|parent
init|=
name|propertyLocation
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to determine definition of authorizable property at "
operator|+
name|propertyLocation
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ReadOnlyNodeTypeManager
name|nodeTypeManager
init|=
name|authorizable
operator|.
name|getUserManager
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|PropertyDefinition
name|def
init|=
name|nodeTypeManager
operator|.
name|getDefinition
argument_list|(
name|parent
argument_list|,
name|property
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|.
name|isProtected
argument_list|()
operator|||
operator|(
name|authorizablePath
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
operator|&&
operator|!
name|def
operator|.
name|getDeclaringNodeType
argument_list|()
operator|.
name|isNodeType
argument_list|(
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE
argument_list|)
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// else: non-protected property somewhere in the subtree of the user tree.
return|return
name|property
return|;
block|}
specifier|private
name|void
name|checkProtectedProperty
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ReadOnlyNodeTypeManager
name|nodeTypeManager
init|=
name|authorizable
operator|.
name|getUserManager
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|PropertyDefinition
name|def
init|=
name|nodeTypeManager
operator|.
name|getDefinition
argument_list|(
name|parent
argument_list|,
name|property
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|.
name|isProtected
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Attempt to set an protected property "
operator|+
name|property
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Retrieves the node at {@code relPath} relative to node associated with      * this authorizable. If no such node exist it and any missing intermediate      * nodes are created.      *      * @param relPath A relative path.      * @return The corresponding node.      * @throws RepositoryException If an error occurs or if {@code relPath} refers      *                             to a node that is outside of the scope of this authorizable.      */
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getOrCreateTargetTree
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|targetTree
decl_stmt|;
name|Tree
name|userTree
init|=
name|getTree
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
name|userTree
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|targetTree
operator|=
name|getLocation
argument_list|(
name|userTree
argument_list|,
name|relPath
argument_list|)
operator|.
name|getTree
argument_list|()
expr_stmt|;
if|if
condition|(
name|targetTree
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|userPath
argument_list|,
name|targetTree
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
name|targetTree
operator|=
operator|new
name|NodeUtil
argument_list|(
name|userTree
argument_list|)
operator|.
name|getOrAddTree
argument_list|(
name|relPath
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
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
name|targetTree
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
block|}
else|else
block|{
name|targetTree
operator|=
name|userTree
expr_stmt|;
block|}
return|return
name|targetTree
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|TreeLocation
name|getLocation
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|relativePath
parameter_list|)
block|{
return|return
name|TreeUtil
operator|.
name|getTreeLocation
argument_list|(
name|tree
argument_list|,
name|relativePath
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|checkRelativePath
parameter_list|(
name|String
name|relativePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|relativePath
operator|==
literal|null
operator|||
name|relativePath
operator|.
name|isEmpty
argument_list|()
operator|||
name|relativePath
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Relative path expected. Found "
operator|+
name|relativePath
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

