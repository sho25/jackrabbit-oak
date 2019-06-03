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
import|import static
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
name|RegistrationConstants
operator|.
name|OAK_SECURITY_NAME
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|AbstractRestrictionProvider
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
name|CompositePattern
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
name|RestrictionDefinitionImpl
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
name|RestrictionPattern
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
name|Component
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
comment|/**  * Default restriction provider implementation that supports the following  * restrictions:  *  *<ul>  *<li>{@link #REP_GLOB}: A simple paths matching pattern. See {@link GlobPattern}  *     for details.</li>  *<li>{@link #REP_NT_NAMES}: A restriction that allows to limit the effect  *     of a given access control entries to JCR nodes of any of the specified  *     primary node type. In case of a JCR property the primary type of the  *     parent node is taken into consideration when evaluating the permissions.</li>  *<li>{@link #REP_PREFIXES}: A multivalued access control restriction  *     which matches by name space prefix. The corresponding restriction type  *     is {@link org.apache.jackrabbit.oak.api.Type#STRINGS}.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|service
operator|=
name|RestrictionProvider
operator|.
name|class
argument_list|,
name|property
operator|=
name|OAK_SECURITY_NAME
operator|+
literal|"=org.apache.jackrabbit.oak.security.authorization.restriction.RestrictionProviderImpl"
argument_list|)
specifier|public
class|class
name|RestrictionProviderImpl
extends|extends
name|AbstractRestrictionProvider
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
name|RestrictionProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_DEFINITIONS
init|=
literal|3
decl_stmt|;
specifier|public
name|RestrictionProviderImpl
parameter_list|()
block|{
name|super
argument_list|(
name|supportedRestrictions
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|RestrictionDefinition
argument_list|>
name|supportedRestrictions
parameter_list|()
block|{
name|RestrictionDefinition
name|glob
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_GLOB
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RestrictionDefinition
name|nts
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_NT_NAMES
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RestrictionDefinition
name|pfxs
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_PREFIXES
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|RestrictionDefinition
name|names
init|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_ITEM_NAMES
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
name|glob
operator|.
name|getName
argument_list|()
argument_list|,
name|glob
argument_list|,
name|nts
operator|.
name|getName
argument_list|()
argument_list|,
name|nts
argument_list|,
name|pfxs
operator|.
name|getName
argument_list|()
argument_list|,
name|pfxs
argument_list|,
name|names
operator|.
name|getName
argument_list|()
argument_list|,
name|names
argument_list|)
return|;
block|}
comment|//------------------------------------------------< RestrictionProvider>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|oakPath
operator|==
literal|null
condition|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|RestrictionPattern
argument_list|>
name|patterns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|NUMBER_OF_DEFINITIONS
argument_list|)
decl_stmt|;
name|PropertyState
name|glob
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
decl_stmt|;
if|if
condition|(
name|glob
operator|!=
literal|null
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
name|GlobPattern
operator|.
name|create
argument_list|(
name|oakPath
argument_list|,
name|glob
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|ntNames
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|REP_NT_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ntNames
operator|!=
literal|null
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
operator|new
name|NodeTypePattern
argument_list|(
name|ntNames
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|prefixes
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|REP_PREFIXES
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixes
operator|!=
literal|null
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
operator|new
name|PrefixPattern
argument_list|(
name|prefixes
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|itemNames
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|REP_ITEM_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|itemNames
operator|!=
literal|null
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
operator|new
name|ItemNamePattern
argument_list|(
name|itemNames
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|CompositePattern
operator|.
name|create
argument_list|(
name|patterns
argument_list|)
return|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
if|if
condition|(
name|oakPath
operator|==
literal|null
operator|||
name|restrictions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|RestrictionPattern
operator|.
name|EMPTY
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|RestrictionPattern
argument_list|>
name|patterns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|NUMBER_OF_DEFINITIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|Restriction
name|r
range|:
name|restrictions
control|)
block|{
name|String
name|name
init|=
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|REP_GLOB
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
name|GlobPattern
operator|.
name|create
argument_list|(
name|oakPath
argument_list|,
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_NT_NAMES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
operator|new
name|NodeTypePattern
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_PREFIXES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
operator|new
name|PrefixPattern
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_ITEM_NAMES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|patterns
operator|.
name|add
argument_list|(
operator|new
name|ItemNamePattern
argument_list|(
name|r
operator|.
name|getProperty
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring unsupported restriction {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CompositePattern
operator|.
name|create
argument_list|(
name|patterns
argument_list|)
return|;
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
annotation|@
name|NotNull
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|super
operator|.
name|validateRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
name|Tree
name|restrictionsTree
init|=
name|getRestrictionsTree
argument_list|(
name|aceTree
argument_list|)
decl_stmt|;
name|PropertyState
name|glob
init|=
name|restrictionsTree
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
decl_stmt|;
if|if
condition|(
name|glob
operator|!=
literal|null
condition|)
block|{
name|GlobPattern
operator|.
name|validate
argument_list|(
name|glob
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

