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
name|authorization
operator|.
name|restriction
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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
name|NamespaceRegistry
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
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
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
name|ImmutableMap
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
name|ImmutableSet
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
name|security
operator|.
name|authorization
operator|.
name|AccessControlConstants
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
name|restriction
operator|.
name|Restriction
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
name|restriction
operator|.
name|RestrictionDefinition
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
name|restriction
operator|.
name|RestrictionProvider
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * RestrictionProviderImpl... TODO  */
end_comment

begin_class
specifier|public
class|class
name|RestrictionProviderImpl
implements|implements
name|RestrictionProvider
implements|,
name|AccessControlConstants
block|{
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|RestrictionDefinition
argument_list|>
name|supported
decl_stmt|;
specifier|public
name|RestrictionProviderImpl
parameter_list|(
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|RestrictionDefinition
name|glob
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_GLOB
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|,
literal|false
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|this
operator|.
name|supported
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|REP_GLOB
argument_list|,
name|glob
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------< RestrictionProvider>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|getSupportedRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
if|if
condition|(
name|isUnsupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|supported
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Restriction
name|createRestriction
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|String
name|jcrName
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|isUnsupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported restriction: "
operator|+
name|oakPath
argument_list|)
throw|;
block|}
name|String
name|oakName
init|=
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
name|RestrictionDefinition
name|definition
init|=
name|supported
operator|.
name|get
argument_list|(
name|oakName
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported restriction: "
operator|+
name|oakPath
argument_list|)
throw|;
block|}
name|int
name|requiredType
init|=
name|definition
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|requiredType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
operator|&&
name|requiredType
operator|!=
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported restriction: Expected value of type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|definition
operator|.
name|getRequiredType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|PropertyState
name|propertyState
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|oakName
argument_list|,
name|value
argument_list|)
decl_stmt|;
return|return
name|createRestriction
argument_list|(
name|propertyState
argument_list|,
name|definition
operator|.
name|isMandatory
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Restriction
argument_list|>
name|readRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|Tree
name|aceTree
parameter_list|)
block|{
if|if
condition|(
name|isUnsupportedPath
argument_list|(
name|oakPath
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
operator|new
name|HashSet
argument_list|<
name|Restriction
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|propertyState
range|:
name|getRestrictionsTree
argument_list|(
name|aceTree
argument_list|)
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|propName
init|=
name|propertyState
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isRestrictionProperty
argument_list|(
name|propName
argument_list|)
operator|&&
name|supported
operator|.
name|containsKey
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|RestrictionDefinition
name|def
init|=
name|supported
operator|.
name|get
argument_list|(
name|propName
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|.
name|getRequiredType
argument_list|()
operator|==
name|propertyState
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
name|restrictions
operator|.
name|add
argument_list|(
name|createRestriction
argument_list|(
name|propertyState
argument_list|,
name|def
operator|.
name|isMandatory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|restrictions
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|Tree
name|aceTree
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
comment|// validation of the restrictions is delegated to the commit hook
comment|// see #validateRestrictions below
if|if
condition|(
operator|!
name|restrictions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NodeUtil
name|aceNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|aceTree
argument_list|)
decl_stmt|;
name|NodeUtil
name|rNode
init|=
name|aceNode
operator|.
name|getOrAddChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|Restriction
name|restriction
range|:
name|restrictions
control|)
block|{
name|rNode
operator|.
name|getTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|validateRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|restrictionProperties
init|=
name|getRestrictionProperties
argument_list|(
name|aceTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|isUnsupportedPath
argument_list|(
name|oakPath
argument_list|)
operator|&&
operator|!
name|restrictionProperties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Restrictions not supported with 'null' path."
argument_list|)
throw|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|entry
range|:
name|restrictionProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|restrName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|RestrictionDefinition
name|def
init|=
name|supported
operator|.
name|get
argument_list|(
name|restrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unsupported restriction: "
operator|+
name|restrName
argument_list|)
throw|;
block|}
name|int
name|type
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|def
operator|.
name|getRequiredType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Invalid restriction type '"
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|type
argument_list|)
operator|+
literal|"'. Expected "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|RestrictionDefinition
name|def
range|:
name|supported
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|def
operator|.
name|isMandatory
argument_list|()
operator|&&
operator|!
name|restrictionProperties
operator|.
name|containsKey
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Mandatory restriction "
operator|+
name|def
operator|.
name|getName
argument_list|()
operator|+
literal|" is missing."
argument_list|)
throw|;
block|}
block|}
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nonnull
specifier|private
name|Restriction
name|createRestriction
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|,
name|boolean
name|isMandatory
parameter_list|)
block|{
return|return
operator|new
name|RestrictionImpl
argument_list|(
name|propertyState
argument_list|,
name|isMandatory
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getRestrictionsTree
parameter_list|(
name|Tree
name|aceTree
parameter_list|)
block|{
name|Tree
name|restrictions
init|=
name|aceTree
operator|.
name|getChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|restrictions
operator|==
literal|null
condition|)
block|{
comment|// no rep:restrictions tree -> read from aceTree for backwards compatibility
name|restrictions
operator|=
name|aceTree
expr_stmt|;
block|}
return|return
name|restrictions
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|getRestrictionProperties
parameter_list|(
name|Tree
name|aceTree
parameter_list|)
block|{
name|Tree
name|rTree
init|=
name|getRestrictionsTree
argument_list|(
name|aceTree
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|restrictionProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|rTree
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isRestrictionProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|restrictionProperties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|restrictionProperties
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isRestrictionProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
operator|!
name|AccessControlConstants
operator|.
name|ACE_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|propertyName
argument_list|)
operator|&&
operator|!
name|NamespaceRegistry
operator|.
name|PREFIX_JCR
operator|.
name|equals
argument_list|(
name|Text
operator|.
name|getNamespacePrefix
argument_list|(
name|propertyName
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isUnsupportedPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|oakPath
operator|==
literal|null
return|;
block|}
block|}
end_class

end_unit

