begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|search
operator|.
name|spi
operator|.
name|editor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A {@link DocumentMaker} is responsible for creating an instance of a document {@link D} to be indexed.  * For Apache Lucene that would be a Lucene {@code Document}, for Apache Solr that might be a {@code SolrInputDocument}, etc.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DocumentMaker
parameter_list|<
name|D
parameter_list|>
block|{
comment|/**    * create a document from the current state and list of modified properties    * @param state the node state    * @param isUpdate whether it is an update or not    * @param propertiesModified the list of modified properties    * @return a document to be indexed    * @throws IOException whether node state read operations or document creation fail    */
name|D
name|makeDocument
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|boolean
name|isUpdate
parameter_list|,
name|List
argument_list|<
name|PropertyState
argument_list|>
name|propertiesModified
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

