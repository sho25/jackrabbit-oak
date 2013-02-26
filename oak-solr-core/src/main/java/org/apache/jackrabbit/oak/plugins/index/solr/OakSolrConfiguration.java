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
comment|/**      * Provide a field name to be used for indexing / searching a certain {@link Type}      *      * @param propertyType the {@link Type} to be indexed / searched      * @return a<code>String</code> representing the Solr field to be used for the given {@link Type}.      */
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
function_decl|;
comment|/**      * Provide the field name for indexing / searching paths      *      * @return a<code>String</code> representing the Solr field to be used for paths.      */
specifier|public
name|String
name|getPathField
parameter_list|()
function_decl|;
comment|/**      * Provide a field name to search over for the given {@link Filter.PathRestriction}      *      * @param pathRestriction the {@link Filter.PathRestriction} used for filtering search results      * @return the field name as a<code>String</code> to be used by Solr for the given restriction      */
specifier|public
name|String
name|getFieldForPathRestriction
parameter_list|(
name|Filter
operator|.
name|PathRestriction
name|pathRestriction
parameter_list|)
function_decl|;
comment|/**      * Provide a field name to search over for the given {@link Filter.PropertyRestriction}      *      * @param propertyRestriction the {@link Filter.PropertyRestriction} used for filtering search results      * @return the field name as a<code>String</code> to be used by Solr for the given restriction      */
specifier|public
name|String
name|getFieldForPropertyRestriction
parameter_list|(
name|Filter
operator|.
name|PropertyRestriction
name|propertyRestriction
parameter_list|)
function_decl|;
comment|/**      * Provide the commit policy to be used with the underlying Solr instance      *      * @return a {@link CommitPolicy}      */
specifier|public
name|CommitPolicy
name|getCommitPolicy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

