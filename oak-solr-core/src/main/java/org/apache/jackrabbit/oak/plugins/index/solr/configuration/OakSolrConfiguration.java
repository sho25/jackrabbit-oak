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
name|Collection
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
name|query
operator|.
name|Filter
import|;
end_import

begin_comment
comment|/**  * A Solr configuration holding all the possible customizable parameters that  * can be leveraged for an Oak search index.  */
end_comment

begin_interface
specifier|public
interface|interface
name|OakSolrConfiguration
block|{
comment|/**      * Provide a field name to be used for indexing / searching a certain {@link org.apache.jackrabbit.oak.api.Type}      *      * @param propertyType the {@link org.apache.jackrabbit.oak.api.Type} to be indexed / searched      * @return the name of the Solr field to be used for the given {@link org.apache.jackrabbit.oak.api.Type}, or {@code null}      * if no specific field has been configured to handle the given {@code Type}.      */
annotation|@
name|CheckForNull
name|String
name|getFieldNameFor
parameter_list|(
name|Type
argument_list|<
name|?
argument_list|>
name|propertyType
parameter_list|)
function_decl|;
comment|/**      * Provide the field name for indexing / searching paths      *      * @return the name of the Solr field to be used for indexing and searching on paths (exact matching).      */
annotation|@
name|Nonnull
name|String
name|getPathField
parameter_list|()
function_decl|;
comment|/**      * Provide a field name to search over for the given {@link org.apache.jackrabbit.oak.spi.query.Filter.PathRestriction}      *      * @param pathRestriction the {@link org.apache.jackrabbit.oak.spi.query.Filter.PathRestriction} used for filtering      *                        search results or {@code null} if no specific field has been configured for it.      * @return the name of the Solr field to be used for the given {@code PathRestriction}.      */
annotation|@
name|CheckForNull
name|String
name|getFieldForPathRestriction
parameter_list|(
name|Filter
operator|.
name|PathRestriction
name|pathRestriction
parameter_list|)
function_decl|;
comment|/**      * Provide a field name to search over for the given {@link org.apache.jackrabbit.oak.spi.query.Filter.PropertyRestriction}      *      * @param propertyRestriction the {@link org.apache.jackrabbit.oak.spi.query.Filter.PropertyRestriction} used for filtering search results      * @return the name of the Solr field to be used for the given {@code PropertyRestriction} or {@code null} if no specific field      * has been configured for it.      */
annotation|@
name|CheckForNull
name|String
name|getFieldForPropertyRestriction
parameter_list|(
name|Filter
operator|.
name|PropertyRestriction
name|propertyRestriction
parameter_list|)
function_decl|;
comment|/**      * Provide the commit policy to be used by a given {@link org.apache.solr.client.solrj.SolrServer}      *      * @return a {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.OakSolrConfiguration.CommitPolicy}      */
annotation|@
name|Nonnull
name|CommitPolicy
name|getCommitPolicy
parameter_list|()
function_decl|;
comment|/**      * Provide a field name that is used as the default "catch all" field for searching over all the data      *      * @return the name of the Solr field to be used as "catch all" field, or {@code null} if no specific field      * has been configured for it.      */
annotation|@
name|CheckForNull
name|String
name|getCatchAllField
parameter_list|()
function_decl|;
comment|/**      * Provide the number of documents (rows) to be fetched for each Solr query      *      * @return the number of rows to fetch      */
name|int
name|getRows
parameter_list|()
function_decl|;
comment|/**      * Define if the Solr index should be used to address property restrictions      *      * @return<code>true</code> if {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex} should be used      * for {@link org.apache.jackrabbit.oak.spi.query.Filter.PropertyRestriction}s      */
name|boolean
name|useForPropertyRestrictions
parameter_list|()
function_decl|;
comment|/**      * Define if the Solr index should be used to filter by primary types      *      * @return<code>true</code> if {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex} should be used      * for filtering by primary types      */
name|boolean
name|useForPrimaryTypes
parameter_list|()
function_decl|;
comment|/**      * Define if the Solr index should be used to filter by path restrictions      *      * @return<code>true</code> if {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex} should be used      * for filtering by {@link org.apache.jackrabbit.oak.spi.query.Filter.PathRestriction}s      */
name|boolean
name|useForPathRestrictions
parameter_list|()
function_decl|;
comment|/**      * Provide the names of the properties that should be neither indexed nor searched by the Solr index      *      * @return a {@link java.util.Collection} of property names for properties to be ignored      */
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|String
argument_list|>
name|getIgnoredProperties
parameter_list|()
function_decl|;
comment|/**      * Provide the names of the properties that should be indexed and searched by the Solr index      *      * @return a {@link java.util.Collection} of property names for properties to be ignored      */
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|String
argument_list|>
name|getUsedProperties
parameter_list|()
function_decl|;
comment|/**      * Make all nodes / documents matching a query that are descendants of a 'jcr:content' node collapse into such a      * node. That will result in resultsets being tipically much more compact in cases where most / all of the matching      * nodes match such a hierarchy.      *      * @return {@code true} if only the 'jcr:content' node should be returned for all its the matching descendants,      * {@code false} otherwise      */
name|boolean
name|collapseJcrContentNodes
parameter_list|()
function_decl|;
comment|/**      * Provide the name of the field to be used for indexing the collapsed path of each node when {@link #collapseJcrContentNodes()}      * is set to {@code true}.      *      * @return the name of the Solr field to be used for indexing and searching on collapsed paths.      */
annotation|@
name|Nonnull
name|String
name|getCollapsedPathField
parameter_list|()
function_decl|;
comment|/**      * Provide the name of the field containing information about the depth of a certain path / node      *      * @return the name of the Solr field to be used for indexing and searching on path depth.      */
name|String
name|getPathDepthField
parameter_list|()
function_decl|;
comment|/**      * Enum for describing Solr commit policy used in a certain instance      */
enum|enum
name|CommitPolicy
block|{
comment|/**          * for default Solr commit          */
name|HARD
block|,
comment|/**          * for Solr soft commit          */
name|SOFT
block|,
comment|/**          * if no commits should be sent (relying on auto(soft)commit on the instance itself)          */
name|AUTO
block|}
block|}
end_interface

end_unit

