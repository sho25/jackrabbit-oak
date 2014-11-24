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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|configuration
package|;
end_package

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
name|Collection
import|;
end_import

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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|server
operator|.
name|EmbeddedSolrServerProvider
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
name|query
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * An {@link OakSolrConfiguration} specified via a given {@link org.apache.jackrabbit.oak.spi.state.NodeState}.  * For each of the supported properties a default is provided if either the  * property doesn't exist in the node or if the value is<code>null</code> or  * empty<code>String</code>.  *<p/>  * Subclasses of this should at least provide the {@link org.apache.jackrabbit.oak.spi.state.NodeState} which holds  * the configuration.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OakSolrNodeStateConfiguration
implements|implements
name|OakSolrConfiguration
implements|,
name|SolrServerConfigurationProvider
argument_list|<
name|EmbeddedSolrServerProvider
argument_list|>
block|{
comment|/**      * get the {@link org.apache.jackrabbit.oak.spi.state.NodeState} which contains the properties for the Oak -      * Solr configuration.      *      * @return a (possibly non-existent) node state for the Solr configuration      */
specifier|protected
specifier|abstract
name|NodeState
name|getConfigurationNodeState
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
name|String
name|getFieldNameFor
parameter_list|(
name|Type
argument_list|<
name|?
argument_list|>
name|propertyType
parameter_list|)
block|{
if|if
condition|(
name|Type
operator|.
name|BINARIES
operator|.
name|equals
argument_list|(
name|propertyType
argument_list|)
operator|||
name|Type
operator|.
name|BINARY
operator|.
name|equals
argument_list|(
name|propertyType
argument_list|)
condition|)
block|{
comment|// TODO : use Tika / SolrCell here
return|return
name|propertyType
operator|.
name|toString
argument_list|()
operator|+
literal|"_bin"
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathField
parameter_list|()
block|{
return|return
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|PATH_FIELD
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|PATH_FIELD_NAME
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFieldForPathRestriction
parameter_list|(
name|Filter
operator|.
name|PathRestriction
name|pathRestriction
parameter_list|)
block|{
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|ALL_CHILDREN
case|:
block|{
name|fieldName
operator|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|DESCENDANTS_FIELD
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|DESC_FIELD_NAME
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|DIRECT_CHILDREN
case|:
block|{
name|fieldName
operator|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|CHILDREN_FIELD
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|CHILD_FIELD_NAME
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|EXACT
case|:
block|{
name|fieldName
operator|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|PATH_FIELD
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|PATH_FIELD_NAME
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|PARENT
case|:
block|{
name|fieldName
operator|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|PARENT_FIELD
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|ANC_FIELD_NAME
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|NO_RESTRICTION
case|:
break|break;
default|default:
break|break;
block|}
return|return
name|fieldName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCatchAllField
parameter_list|()
block|{
return|return
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|CATCHALL_FIELD
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|CATCHALL_FIELD
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFieldForPropertyRestriction
parameter_list|(
name|Filter
operator|.
name|PropertyRestriction
name|propertyRestriction
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|CommitPolicy
name|getCommitPolicy
parameter_list|()
block|{
return|return
name|CommitPolicy
operator|.
name|valueOf
argument_list|(
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|COMMIT_POLICY
argument_list|,
name|CommitPolicy
operator|.
name|SOFT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getRows
parameter_list|()
block|{
return|return
name|getIntValueFor
argument_list|(
name|Properties
operator|.
name|ROWS
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|ROWS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|useForPropertyRestrictions
parameter_list|()
block|{
return|return
name|getBooleanValueFor
argument_list|(
name|Properties
operator|.
name|PROPERTY_RESTRICIONS
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|PROPERTY_RESTRICTIONS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|useForPrimaryTypes
parameter_list|()
block|{
return|return
name|getBooleanValueFor
argument_list|(
name|Properties
operator|.
name|PRIMARY_TYPES
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|PRIMARY_TYPES
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|useForPathRestrictions
parameter_list|()
block|{
return|return
name|getBooleanValueFor
argument_list|(
name|Properties
operator|.
name|PATH_RESTRICTIONS
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|PATH_RESTRICTIONS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getIgnoredProperties
parameter_list|()
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|ignoredProperties
decl_stmt|;
name|String
name|ignoredPropertiesString
init|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|IGNORED_PROPERTIES
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|IGNORED_PROPERTIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ignoredPropertiesString
operator|!=
literal|null
condition|)
block|{
name|ignoredProperties
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|ignoredPropertiesString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ignoredProperties
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
return|return
name|ignoredProperties
return|;
block|}
specifier|private
name|boolean
name|getBooleanValueFor
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
name|boolean
name|value
init|=
name|defaultValue
decl_stmt|;
name|NodeState
name|configurationNodeState
init|=
name|getConfigurationNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|configurationNodeState
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|property
init|=
name|configurationNodeState
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|value
return|;
block|}
specifier|private
name|int
name|getIntValueFor
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
name|long
name|value
init|=
name|defaultValue
decl_stmt|;
name|NodeState
name|configurationNodeState
init|=
name|getConfigurationNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|configurationNodeState
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|property
init|=
name|configurationNodeState
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
specifier|private
name|String
name|getStringValueFor
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|defaultValue
decl_stmt|;
name|NodeState
name|configurationNodeState
init|=
name|getConfigurationNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|configurationNodeState
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|property
init|=
name|configurationNodeState
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|SolrServerConfiguration
argument_list|<
name|EmbeddedSolrServerProvider
argument_list|>
name|getSolrServerConfiguration
parameter_list|()
block|{
name|String
name|solrHomePath
init|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|SOLRHOME_PATH
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|SOLR_HOME_PATH
argument_list|)
decl_stmt|;
name|String
name|solrConfigPath
init|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|SOLRCONFIG_PATH
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|SOLR_CONFIG_PATH
argument_list|)
decl_stmt|;
name|String
name|coreName
init|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|CORE_NAME
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|CORE_NAME
argument_list|)
decl_stmt|;
name|String
name|context
init|=
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|CONTEXT
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|CONTEXT
argument_list|)
decl_stmt|;
name|Integer
name|httpPort
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|HTTP_PORT
argument_list|,
name|SolrServerConfigurationDefaults
operator|.
name|HTTP_PORT
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EmbeddedSolrServerConfiguration
argument_list|(
name|solrHomePath
argument_list|,
name|solrConfigPath
argument_list|,
name|coreName
argument_list|)
operator|.
name|withHttpConfiguration
argument_list|(
name|context
argument_list|,
name|httpPort
argument_list|)
return|;
block|}
comment|/**      * Properties that may be retrieved from the configuration {@link org.apache.jackrabbit.oak.spi.state.NodeState}.      */
specifier|public
specifier|final
class|class
name|Properties
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SOLRHOME_PATH
init|=
literal|"solrHomePath"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SOLRCONFIG_PATH
init|=
literal|"solrConfigPath"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONTEXT
init|=
literal|"solrContext"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_PORT
init|=
literal|"httpPort"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CORE_NAME
init|=
literal|"coreName"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATH_FIELD
init|=
literal|"pathField"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PARENT_FIELD
init|=
literal|"parentField"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CHILDREN_FIELD
init|=
literal|"childrenField"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DESCENDANTS_FIELD
init|=
literal|"descendantsField"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CATCHALL_FIELD
init|=
literal|"catchAllField"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_POLICY
init|=
literal|"commitPolicy"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ROWS
init|=
literal|"rows"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_RESTRICIONS
init|=
literal|"propertyRestrictions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PRIMARY_TYPES
init|=
literal|"primaryTypes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATH_RESTRICTIONS
init|=
literal|"pathRestrictions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|IGNORED_PROPERTIES
init|=
literal|"ignoredProperties"
decl_stmt|;
block|}
block|}
end_class

end_unit

