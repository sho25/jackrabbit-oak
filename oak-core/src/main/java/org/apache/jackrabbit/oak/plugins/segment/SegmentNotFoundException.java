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
name|segment
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
name|IllegalRepositoryStateException
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when there the segment does not exist in the store  */
end_comment

begin_class
specifier|public
class|class
name|SegmentNotFoundException
extends|extends
name|IllegalRepositoryStateException
block|{
specifier|private
specifier|final
name|String
name|segmentId
decl_stmt|;
specifier|public
name|SegmentNotFoundException
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
name|super
argument_list|(
literal|"Segment "
operator|+
name|id
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentId
operator|=
name|id
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SegmentNotFoundException
parameter_list|(
name|SegmentId
name|id
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|super
argument_list|(
literal|"Segment "
operator|+
name|id
operator|+
literal|" not found"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentId
operator|=
name|id
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getSegmentId
parameter_list|()
block|{
return|return
name|segmentId
return|;
block|}
block|}
end_class

end_unit

