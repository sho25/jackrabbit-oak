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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
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
name|Maps
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
name|principal
operator|.
name|ItemBasedPrincipal
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
name|SecurityProvider
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
name|authorization
operator|.
name|principalbased
operator|.
name|Filter
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
name|authorization
operator|.
name|principalbased
operator|.
name|FilterProvider
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
name|principal
operator|.
name|PrincipalConfiguration
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
name|principal
operator|.
name|PrincipalProvider
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
name|principal
operator|.
name|SystemUserPrincipal
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
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Modified
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Designate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
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
name|java
operator|.
name|security
operator|.
name|Principal
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
name|Set
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
name|checkState
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@link org.apache.jackrabbit.oak.spi.security.authorization.principalbased.Filter} interface that  * consists of the following two filtering conditions:  *  *<ol>  *<li>All principals in the set must be of type {@link org.apache.jackrabbit.oak.spi.security.principal.SystemUserPrincipal}</li>  *<li>All principals in the set must be located in the repository below the configured path.</li>  *</ol>  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|service
operator|=
block|{
name|FilterProvider
operator|.
name|class
block|}
argument_list|,
name|configurationPolicy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|Designate
argument_list|(
name|ocd
operator|=
name|FilterProviderImpl
operator|.
name|Configuration
operator|.
name|class
argument_list|)
specifier|public
class|class
name|FilterProviderImpl
implements|implements
name|FilterProvider
block|{
annotation|@
name|ObjectClassDefinition
argument_list|(
name|name
operator|=
literal|"Apache Jackrabbit Oak Filter for Principal Based Authorization"
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Path"
argument_list|,
name|description
operator|=
literal|"Required path underneath which all filtered system-user-principals must be located in the repository."
argument_list|)
name|String
name|path
parameter_list|()
function_decl|;
block|}
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
name|FilterProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|oakPath
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|validatedPrincipalNamesPathMap
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|unsupportedPrincipalNames
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
comment|//-----------------------------------------------------< FilterProvider>---
annotation|@
name|Override
specifier|public
name|boolean
name|handlesPath
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|this
operator|.
name|oakPath
argument_list|,
name|oakPath
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getFilterRoot
parameter_list|()
block|{
return|return
name|oakPath
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Filter
name|getFilter
parameter_list|(
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|PrincipalProvider
name|principalProvider
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|root
argument_list|,
name|principalProvider
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
comment|//----------------------------------------------------< SCR Integration>---
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|setPath
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Modified
specifier|protected
name|void
name|modified
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|setPath
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setPath
parameter_list|(
annotation|@
name|NotNull
name|Configuration
name|configuration
parameter_list|)
block|{
name|checkState
argument_list|(
name|isValidPath
argument_list|(
name|configuration
operator|.
name|path
argument_list|()
argument_list|)
argument_list|,
literal|"Configured path must be a valid absolute path."
argument_list|)
expr_stmt|;
name|oakPath
operator|=
name|configuration
operator|.
name|path
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isValidPath
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|path
argument_list|)
operator|&&
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|//-------------------------------------------------------------< Filter>---
specifier|private
specifier|final
class|class
name|FilterImpl
implements|implements
name|Filter
block|{
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|PrincipalProvider
name|principalProvider
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
name|FilterImpl
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|PrincipalProvider
name|principalProvider
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|principalProvider
operator|=
name|principalProvider
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canHandle
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
if|if
condition|(
name|principals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
if|if
condition|(
operator|!
name|isValidPrincipal
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|String
name|getOakPath
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|validPrincipal
parameter_list|)
block|{
name|String
name|principalPath
init|=
name|validatedPrincipalNamesPathMap
operator|.
name|get
argument_list|(
name|validPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|principalPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid principal "
operator|+
name|validPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|principalPath
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Principal
name|getValidPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|)
block|{
name|ItemBasedPrincipal
name|principal
init|=
name|principalProvider
operator|.
name|getItemBasedPrincipal
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|principal
operator|!=
literal|null
operator|&&
name|isValidPrincipal
argument_list|(
name|principal
argument_list|)
condition|)
block|{
return|return
name|principal
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|boolean
name|isValidPrincipal
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|principal
operator|instanceof
name|SystemUserPrincipal
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|principalName
init|=
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|validatedPrincipalNamesPathMap
operator|.
name|get
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|isValidMapEntry
argument_list|(
name|principal
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|path
operator|=
name|unsupportedPrincipalNames
operator|.
name|get
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|isValidMapEntry
argument_list|(
name|principal
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|principalPath
init|=
name|getPrincipalPath
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|principalPath
operator|!=
literal|null
operator|&&
name|handlesPath
argument_list|(
name|principalPath
argument_list|)
condition|)
block|{
name|unsupportedPrincipalNames
operator|.
name|remove
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
name|validatedPrincipalNamesPathMap
operator|.
name|put
argument_list|(
name|principalName
argument_list|,
name|principalPath
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|validatedPrincipalNamesPathMap
operator|.
name|remove
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
name|unsupportedPrincipalNames
operator|.
name|put
argument_list|(
name|principalName
argument_list|,
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|principalPath
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**          * Besteffort validation if the given entry in 'validatedPrincipalNamesPathMap' is points to the correct path.          * Note, that this will just be performed for instances of {@code ItemBasedPrincipal}, where obtaining the path          * doesn't require looking up the principal again.          *          * @param principal The target principal to be validated          * @param oakPath The Oak path stored in 'validatedPrincipalNamesPathMap' for the given principal.          * @return {@code true}, if the principal is an instance of {@code ItemBasedPrincipal}, whose Oak path is equal          * to the given {@code oakPath} and {@code false} if the paths are not equal. For any other types of principal          * this method will return {@code true} in order to avoid excessive principal lookup.          */
specifier|private
name|boolean
name|isValidMapEntry
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
return|return
name|oakPath
operator|.
name|equals
argument_list|(
name|getOakPath
argument_list|(
operator|(
name|ItemBasedPrincipal
operator|)
name|principal
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Nullable
specifier|private
name|String
name|getPrincipalPath
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
name|String
name|prinicpalOakPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|principal
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
name|prinicpalOakPath
operator|=
name|getOakPath
argument_list|(
operator|(
name|ItemBasedPrincipal
operator|)
name|principal
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prinicpalOakPath
operator|==
literal|null
operator|||
operator|!
name|root
operator|.
name|getTree
argument_list|(
name|prinicpalOakPath
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// the given principal is not ItemBasedPrincipal or it has been obtained with a different name-path-mapper
comment|// (making the conversion to oak-path return null) or it has been moved and the path no longer points to
comment|// an existing tree -> try looking up principal by name
name|Principal
name|p
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
name|prinicpalOakPath
operator|=
name|getOakPath
argument_list|(
operator|(
name|ItemBasedPrincipal
operator|)
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prinicpalOakPath
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|prinicpalOakPath
return|;
block|}
annotation|@
name|Nullable
specifier|private
name|String
name|getOakPath
parameter_list|(
annotation|@
name|NotNull
name|ItemBasedPrincipal
name|principal
parameter_list|)
block|{
try|try
block|{
return|return
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|principal
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error while retrieving path from ItemBasedPrincipal {}, {}"
argument_list|,
name|principal
operator|.
name|getName
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

