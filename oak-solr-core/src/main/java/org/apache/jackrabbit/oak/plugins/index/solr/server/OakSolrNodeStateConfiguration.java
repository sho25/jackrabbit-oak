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
name|server
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
comment|/**  * An {@link OakSolrConfiguration} specified via a given {@link NodeState}.  * For each of the supported properties a default is provided if either the  * property doesn't exist in the node or if the value is<code>null</code> or  * empty<code>String</code>.  */
end_comment

begin_class
specifier|public
class|class
name|OakSolrNodeStateConfiguration
implements|implements
name|OakSolrConfiguration
block|{
specifier|private
name|NodeState
name|solrConfigurationNodeState
decl_stmt|;
specifier|public
name|OakSolrNodeStateConfiguration
parameter_list|(
name|NodeState
name|solrConfigurationNodeState
parameter_list|)
block|{
name|this
operator|.
name|solrConfigurationNodeState
operator|=
name|solrConfigurationNodeState
expr_stmt|;
block|}
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
literal|"path_exact"
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
literal|"path_des"
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
literal|"path_child"
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
literal|"path_exact"
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
literal|"path_anc"
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
name|HARD
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSolrHomePath
parameter_list|()
block|{
return|return
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|SOLRHOME_PATH
argument_list|,
literal|"./"
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSolrConfigPath
parameter_list|()
block|{
return|return
name|getStringValueFor
argument_list|(
name|Properties
operator|.
name|SOLRCONFIG_PATH
argument_list|,
literal|"./solr.xml"
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
literal|"oak"
argument_list|)
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
literal|null
decl_stmt|;
name|PropertyState
name|property
init|=
name|solrConfigurationNodeState
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
return|return
name|value
return|;
block|}
comment|/**      * Properties that may be retrieved from the configuration {@link NodeState}.      */
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

