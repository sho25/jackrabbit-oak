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
name|lucene
operator|.
name|spi
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_comment
comment|/**  * Implementations of this interface would get callbacks while indexing documents. It's the responsibility  * of the implementation to exit as early as possible if it doesn't care about the document being indexed.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexFieldProvider
block|{
comment|/**      * This method would get called while indexing property changes. The method would be called once for each property      * that is changed.      *      * @param path path of the document being indexed      * @param propertyName property name (including relative path, if any) of the changed property      * @param document {@link NodeState} of the document being indexed      * @param property {@link PropertyState} of changed property      * @param indexDefinition {@link NodeState} of index definition      * @return {@link Iterable} of fields that are to be added to {@link org.apache.lucene.document.Document} being prepared      */
name|Iterable
argument_list|<
name|Field
argument_list|>
name|getAugmentedFields
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|propertyName
parameter_list|,
specifier|final
name|NodeState
name|document
parameter_list|,
specifier|final
name|PropertyState
name|property
parameter_list|,
specifier|final
name|NodeState
name|indexDefinition
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

