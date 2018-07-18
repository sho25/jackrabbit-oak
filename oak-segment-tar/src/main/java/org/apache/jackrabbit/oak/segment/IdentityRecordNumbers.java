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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * An implementation of a record number to offset table that assumes that a  * record number is also a valid offset in the segment. This implementation is  * useful when an instance of a table has still to be provided, but record  * numbers have no logical semantics (e.g. for bulk segments).  *<p>  * This implementation is trivially thread-safe.  */
end_comment

begin_class
class|class
name|IdentityRecordNumbers
implements|implements
name|RecordNumbers
block|{
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
return|return
name|recordNumber
return|;
block|}
annotation|@
name|NotNull
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"invalid usage of the record-number-to-offset table"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

