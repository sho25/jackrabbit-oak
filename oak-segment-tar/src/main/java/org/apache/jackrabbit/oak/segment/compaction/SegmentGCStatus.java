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
name|segment
operator|.
name|compaction
package|;
end_package

begin_enum
specifier|public
enum|enum
name|SegmentGCStatus
block|{
name|IDLE
argument_list|(
literal|"idle"
argument_list|)
block|,
name|ESTIMATION
argument_list|(
literal|"estimation"
argument_list|)
block|,
name|COMPACTION
argument_list|(
literal|"compaction"
argument_list|)
block|,
name|COMPACTION_RETRY
argument_list|(
literal|"compaction-retry-"
argument_list|)
block|,
name|COMPACTION_FORCE_COMPACT
argument_list|(
literal|"compaction-force-compact"
argument_list|)
block|,
name|CLEANUP
argument_list|(
literal|"cleanup"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
name|SegmentGCStatus
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|String
name|message
parameter_list|()
block|{
return|return
name|message
return|;
block|}
block|}
end_enum

end_unit

