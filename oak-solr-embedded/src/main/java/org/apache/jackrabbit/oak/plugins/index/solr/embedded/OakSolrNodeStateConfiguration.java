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
name|embedded
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
name|CommitPolicy
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
name|OakSolrConfiguration
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
comment|/**  * An {@link org.apache.jackrabbit.oak.plugins.index.solr.OakSolrConfiguration} specified via a given {@link org.apache.jackrabbit.oak.spi.state.NodeState}.  * For each of the supported properties a default is provided if either the  * property doesn't exist in the node or if the value is<code>null</code> or  * empty<code>String</code>.  *<p/>  * Subclasses of this should at least provide the {@link org.apache.jackrabbit.oak.spi.state.NodeState} which holds  * the configuration.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OakSolrNodeStateConfiguration
extends|extends
name|EmbeddedSolrConfiguration
implements|implements
name|OakSolrConfiguration
implements|,
name|SolrServerConfigurationProvider
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
block|}
return|return
name|fieldName
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
name|String
name|getCoreName
parameter_list|()
block|{
return|return
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
return|;
block|}
specifier|protected
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
literal|null
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
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|value
operator|=
name|defaultValue
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
name|getSolrServerConfiguration
parameter_list|()
block|{
return|return
operator|new
name|SolrServerConfiguration
argument_list|(
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
argument_list|,
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
argument_list|,
name|getCoreName
argument_list|()
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
name|COMMIT_POLICY
init|=
literal|"commitPolicy"
decl_stmt|;
block|}
block|}
end_class

end_unit

