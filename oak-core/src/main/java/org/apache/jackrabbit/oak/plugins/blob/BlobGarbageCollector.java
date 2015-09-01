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
name|blob
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Interface for blob garbage collector  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobGarbageCollector
block|{
comment|/**      * Marks garbage blobs from the passed node store instance.      * Collects them only if markOnly is false.      *      * @param markOnly whether to only mark references and not sweep in the mark and sweep operation.      * @throws Exception the exception      */
name|void
name|collectGarbage
parameter_list|(
name|boolean
name|markOnly
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Retuns the list of stats      *       * @return stats      * @throws Exception      */
name|List
argument_list|<
name|GarbageCollectionRepoStats
argument_list|>
name|getStats
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

