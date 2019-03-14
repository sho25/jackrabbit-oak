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
name|principal
package|;
end_package

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
name|PrincipalIterator
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
name|PrincipalManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Extension for the {@code PrincipalManager} that offers range search.  *  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|PrincipalQueryManager
block|{
comment|/**      * Gets the principals matching a simple filter expression applied against      * the {@link Principal#getName() principal name} AND the specified search      * type.      * Results are expected to be sorted by the principal name.      * An implementation may limit the number of principals returned.      * If there are no matching principals, an empty iterator is returned.      * @param simpleFilter      * @param fullText      * @param searchType Any of the following constants:      *<ul>      *<li>{@link PrincipalManager#SEARCH_TYPE_ALL}</li>      *<li>{@link PrincipalManager#SEARCH_TYPE_GROUP}</li>      *<li>{@link PrincipalManager#SEARCH_TYPE_NOT_GROUP}</li>      *</ul>      * @param offset Offset from where to start returning results.<code>0</code> for no offset.      * @param limit Maximal number of results to return. -1 for no limit.      * @return a<code>PrincipalIterator</code> over the<code>Principal</code>s      * matching the given filter and search type.      */
name|PrincipalIterator
name|findPrincipals
parameter_list|(
name|String
name|simpleFilter
parameter_list|,
name|boolean
name|fullText
parameter_list|,
name|int
name|searchType
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|limit
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

