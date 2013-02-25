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
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|OakSolrConfigurationProvider
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_comment
comment|/**  * An {@link OakSolrConfigurationProvider} for the remote Solr server  *<p/>  * In this {@link OakSolrConfiguration} the 'path' related fields are taken from  * OSGi configuration while the other configuration just does nothing triggering  * the default behavior that properties are indexed by name.  * Possible extensions of this class may trigger type based property indexing / search.  */
end_comment

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|OakSolrConfigurationProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RemoteSolrConfigurationProvider
implements|implements
name|OakSolrConfigurationProvider
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DESC_FIELD
init|=
literal|"path_des"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CHILD_FIELD
init|=
literal|"path_child"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PARENT_FIELD
init|=
literal|"path_anc"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PATH_FIELD
init|=
literal|"path_exact"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_DESC_FIELD
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PATH_DESCENDANTS_FIELD
init|=
literal|"path.desc.field"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_CHILD_FIELD
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PATH_CHILDREN_FIELD
init|=
literal|"path.child.field"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_PARENT_FIELD
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PATH_PARENT_FIELD
init|=
literal|"path.parent.field"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_PATH_FIELD
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PATH_EXACT_FIELD
init|=
literal|"path.exact.field"
decl_stmt|;
specifier|private
name|String
name|pathChildrenFieldName
decl_stmt|;
specifier|private
name|String
name|pathParentFieldName
decl_stmt|;
specifier|private
name|String
name|pathDescendantsFieldName
decl_stmt|;
specifier|private
name|String
name|pathExactFieldName
decl_stmt|;
specifier|private
name|OakSolrConfiguration
name|oakSolrConfiguration
decl_stmt|;
specifier|public
name|RemoteSolrConfigurationProvider
parameter_list|()
block|{
name|this
operator|.
name|pathChildrenFieldName
operator|=
name|DEFAULT_CHILD_FIELD
expr_stmt|;
name|this
operator|.
name|pathDescendantsFieldName
operator|=
name|DEFAULT_DESC_FIELD
expr_stmt|;
name|this
operator|.
name|pathExactFieldName
operator|=
name|DEFAULT_PATH_FIELD
expr_stmt|;
name|this
operator|.
name|pathParentFieldName
operator|=
name|DEFAULT_PARENT_FIELD
expr_stmt|;
block|}
specifier|public
name|RemoteSolrConfigurationProvider
parameter_list|(
name|String
name|pathChildrenFieldName
parameter_list|,
name|String
name|pathParentFieldName
parameter_list|,
name|String
name|pathDescendantsFieldName
parameter_list|,
name|String
name|pathExactFieldName
parameter_list|)
block|{
name|this
operator|.
name|pathChildrenFieldName
operator|=
name|pathChildrenFieldName
expr_stmt|;
name|this
operator|.
name|pathParentFieldName
operator|=
name|pathParentFieldName
expr_stmt|;
name|this
operator|.
name|pathDescendantsFieldName
operator|=
name|pathDescendantsFieldName
expr_stmt|;
name|this
operator|.
name|pathExactFieldName
operator|=
name|pathExactFieldName
expr_stmt|;
block|}
specifier|protected
name|void
name|activate
parameter_list|(
name|ComponentContext
name|componentContext
parameter_list|)
throws|throws
name|Exception
block|{
name|pathChildrenFieldName
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PATH_CHILDREN_FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|pathParentFieldName
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PATH_PARENT_FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|pathExactFieldName
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PATH_EXACT_FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|pathDescendantsFieldName
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|PATH_DESCENDANTS_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OakSolrConfiguration
name|getConfiguration
parameter_list|()
block|{
if|if
condition|(
name|oakSolrConfiguration
operator|==
literal|null
condition|)
block|{
name|oakSolrConfiguration
operator|=
operator|new
name|OakSolrConfiguration
argument_list|()
block|{
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
name|pathExactFieldName
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
name|pathDescendantsFieldName
expr_stmt|;
break|break;
block|}
case|case
name|DIRECT_CHILDREN
case|:
block|{
name|fieldName
operator|=
name|pathChildrenFieldName
expr_stmt|;
break|break;
block|}
case|case
name|EXACT
case|:
block|{
name|fieldName
operator|=
name|pathExactFieldName
expr_stmt|;
break|break;
block|}
case|case
name|PARENT
case|:
block|{
name|fieldName
operator|=
name|pathParentFieldName
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
block|}
expr_stmt|;
block|}
return|return
name|oakSolrConfiguration
return|;
block|}
block|}
end_class

end_unit

