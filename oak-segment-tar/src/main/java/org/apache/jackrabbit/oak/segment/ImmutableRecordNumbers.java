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
name|segment
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * An immutable record table. It is initialized at construction time and can  * never be changed afterwards.  *<p>  * This implementation is trivially thread-safe.  */
end_comment

begin_class
class|class
name|ImmutableRecordNumbers
implements|implements
name|RecordNumbers
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|RecordEntry
argument_list|>
name|records
decl_stmt|;
comment|/**      * Create a new immutable record table.      *      * @param records a map of record numbers to record entries. It can't be      *                {@code null}.      */
name|ImmutableRecordNumbers
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|RecordEntry
argument_list|>
name|records
parameter_list|)
block|{
name|this
operator|.
name|records
operator|=
name|newHashMap
argument_list|(
name|records
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOffset
parameter_list|(
name|int
name|recordNumber
parameter_list|)
block|{
name|RecordEntry
name|entry
init|=
name|records
operator|.
name|get
argument_list|(
name|recordNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|entry
operator|.
name|getOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|RecordNumbersIterator
argument_list|(
name|records
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

