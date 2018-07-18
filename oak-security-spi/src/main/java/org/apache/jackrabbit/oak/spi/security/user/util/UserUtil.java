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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|AuthorizableTypeException
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
name|spi
operator|.
name|security
operator|.
name|ConfigurationParameters
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
name|AuthorizableType
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
name|spi
operator|.
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
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
name|tree
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|checkArgument
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
name|Preconditions
operator|.
name|checkNotNull
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
name|api
operator|.
name|Type
operator|.
name|STRING
import|;
end_import

begin_comment
comment|/**  * Utility methods for user management.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|UserUtil
implements|implements
name|UserConstants
block|{
specifier|private
name|UserUtil
parameter_list|()
block|{     }
specifier|public
specifier|static
name|boolean
name|isAdmin
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|parameters
parameter_list|,
annotation|@
name|NotNull
name|String
name|userId
parameter_list|)
block|{
return|return
name|getAdminId
argument_list|(
name|parameters
argument_list|)
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
specifier|static
name|String
name|getAdminId
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|parameters
parameter_list|)
block|{
return|return
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|PARAM_ADMIN_ID
argument_list|,
name|DEFAULT_ADMIN_ID
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
specifier|static
name|String
name|getAnonymousId
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|parameters
parameter_list|)
block|{
return|return
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|PARAM_ANONYMOUS_ID
argument_list|,
name|DEFAULT_ANONYMOUS_ID
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isType
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|authorizableTree
parameter_list|,
annotation|@
name|NotNull
name|AuthorizableType
name|type
parameter_list|)
block|{
if|if
condition|(
name|authorizableTree
operator|!=
literal|null
condition|)
block|{
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|authorizableTree
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|GROUP
case|:
return|return
name|NT_REP_GROUP
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
return|;
case|case
name|USER
case|:
return|return
name|NT_REP_USER
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|NT_REP_SYSTEM_USER
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
return|;
default|default:
return|return
name|NT_REP_USER
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|NT_REP_GROUP
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|NT_REP_SYSTEM_USER
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
name|AuthorizableType
name|getType
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|authorizableNode
parameter_list|)
block|{
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|authorizableNode
argument_list|)
decl_stmt|;
return|return
name|getType
argument_list|(
name|ntName
argument_list|)
return|;
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
name|AuthorizableType
name|getType
parameter_list|(
annotation|@
name|Nullable
name|String
name|primaryTypeName
parameter_list|)
block|{
if|if
condition|(
name|primaryTypeName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|NT_REP_GROUP
operator|.
name|equals
argument_list|(
name|primaryTypeName
argument_list|)
condition|)
block|{
return|return
name|AuthorizableType
operator|.
name|GROUP
return|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_USER
operator|.
name|equals
argument_list|(
name|primaryTypeName
argument_list|)
condition|)
block|{
return|return
name|AuthorizableType
operator|.
name|USER
return|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_SYSTEM_USER
operator|.
name|equals
argument_list|(
name|primaryTypeName
argument_list|)
condition|)
block|{
return|return
name|AuthorizableType
operator|.
name|USER
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isSystemUser
parameter_list|(
annotation|@
name|Nullable
name|Tree
name|authorizableTree
parameter_list|)
block|{
return|return
name|authorizableTree
operator|!=
literal|null
operator|&&
name|NT_REP_SYSTEM_USER
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|authorizableTree
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
name|String
name|getAuthorizableRootPath
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|parameters
parameter_list|,
annotation|@
name|Nullable
name|AuthorizableType
name|type
parameter_list|)
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|USER
case|:
name|path
operator|=
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
break|break;
case|case
name|GROUP
case|:
name|path
operator|=
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
expr_stmt|;
break|break;
default|default:
name|path
operator|=
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
name|String
name|groupRoot
init|=
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|path
argument_list|,
name|groupRoot
argument_list|)
condition|)
block|{
name|path
operator|=
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|path
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|path
return|;
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
name|String
name|getAuthorizableId
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|authorizableTree
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|authorizableTree
argument_list|)
expr_stmt|;
if|if
condition|(
name|UserUtil
operator|.
name|isType
argument_list|(
name|authorizableTree
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|)
condition|)
block|{
name|PropertyState
name|idProp
init|=
name|authorizableTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|idProp
operator|!=
literal|null
condition|)
block|{
return|return
name|idProp
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Text
operator|.
name|unescapeIllegalJcrChars
argument_list|(
name|authorizableTree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Retrieve the id from the given {@code authorizableTree}, which must have      * been verified for being a valid authorizable of the specified type upfront.      *      * @param authorizableTree The authorizable tree which must be of the given {@code type}/      * @param type The type of the authorizable tree.      * @return The id retrieved from the specified {@code AuthorizableTree}.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|String
name|getAuthorizableId
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|authorizableTree
parameter_list|,
annotation|@
name|NotNull
name|AuthorizableType
name|type
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|UserUtil
operator|.
name|isType
argument_list|(
name|authorizableTree
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyState
name|idProp
init|=
name|authorizableTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|idProp
operator|!=
literal|null
condition|)
block|{
return|return
name|idProp
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Text
operator|.
name|unescapeIllegalJcrChars
argument_list|(
name|authorizableTree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Authorizable
parameter_list|>
name|T
name|castAuthorizable
parameter_list|(
annotation|@
name|Nullable
name|Authorizable
name|authorizable
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|authorizableClass
parameter_list|)
throws|throws
name|AuthorizableTypeException
block|{
if|if
condition|(
name|authorizable
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|authorizableClass
operator|!=
literal|null
operator|&&
name|authorizableClass
operator|.
name|isInstance
argument_list|(
name|authorizable
argument_list|)
condition|)
block|{
return|return
name|authorizableClass
operator|.
name|cast
argument_list|(
name|authorizable
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AuthorizableTypeException
argument_list|(
literal|"Invalid authorizable type '"
operator|+
operator|(
operator|(
name|authorizableClass
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|authorizableClass
operator|)
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
comment|/**      * Return the configured {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior}      * for the given {@code config}. The default behavior in case      * {@link org.apache.jackrabbit.oak.spi.xml.ProtectedItemImporter#PARAM_IMPORT_BEHAVIOR}      * is not contained in the {@code config} object is      * {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior#IGNORE}      *      * @param config The configuration parameters.      * @return The import behavior as defined by {@link org.apache.jackrabbit.oak.spi.xml.ProtectedItemImporter#PARAM_IMPORT_BEHAVIOR}      * or {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior#IGNORE} if this      * config parameter is missing.      */
specifier|public
specifier|static
name|int
name|getImportBehavior
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|String
name|importBehaviorStr
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|ImportBehavior
operator|.
name|NAME_IGNORE
argument_list|)
decl_stmt|;
return|return
name|ImportBehavior
operator|.
name|valueFromString
argument_list|(
name|importBehaviorStr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

