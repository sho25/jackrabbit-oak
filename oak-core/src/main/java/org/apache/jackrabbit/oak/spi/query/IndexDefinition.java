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
name|query
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Defines an index definition  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexDefinition
block|{
name|String
name|TYPE_PROPERTY_NAME
init|=
literal|"type"
decl_stmt|;
name|String
name|UNIQUE_PROPERTY_NAME
init|=
literal|"unique"
decl_stmt|;
name|String
name|INDEX_DATA_CHILD_NAME
init|=
literal|":data"
decl_stmt|;
comment|/**      * Get the unique index name. This is also the name of the index node.      *       * @return the index name      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return the index type      */
annotation|@
name|Nonnull
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * @return the index path, including the name as the last segment      */
annotation|@
name|Nonnull
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * Whether each value may only appear once in the index.      *       * @return true if unique      */
name|boolean
name|isUnique
parameter_list|()
function_decl|;
comment|/**      * @return the index properties      */
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

