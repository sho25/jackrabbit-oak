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
name|Iterator
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
name|Result
operator|.
name|SizePrecision
import|;
end_import

begin_comment
comment|/**  * A cursor to read a number of nodes sequentially.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cursor
extends|extends
name|Iterator
argument_list|<
name|IndexRow
argument_list|>
block|{
comment|/**      * The next row within this index.      *<p>      * The row may only contains the path, if a path is available. It may also      * (or just) contain so-called "pseudo-properties" such as "jcr:score" and      * "rep:excerpt", in case the index supports those properties and if the      * properties were requested when running the query. The query engine will      * indicate that those pseudo properties were requested by setting an      * appropriate (possibly unrestricted) filter condition.      *<p>      * The index should return a row with those properties that are stored in      * the index itself, so that the query engine doesn't have to load the whole      * row / node unnecessarily (avoiding to load the whole row is sometimes      * called "index only scan"), specially for rows that are anyway skipped. If      * the index does not have an (efficient) way to return some (or any) of the      * properties, it doesn't have to provide those values. In this case, the      * query engine will load the node itself if required. If all conditions      * match, the query engine will sometimes load the node to do access checks,      * but this is not always the case, and it is not the case if any of the      * (join) conditions do not match.      *       * @return the row      */
annotation|@
name|Override
name|IndexRow
name|next
parameter_list|()
function_decl|;
comment|/**      * Get the size if known.      *       * @param precision the required precision      * @param max the maximum nodes read (for an exact size)      * @return the size, or -1 if unknown      */
name|long
name|getSize
parameter_list|(
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

