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
name|document
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
name|plugins
operator|.
name|document
operator|.
name|cache
operator|.
name|CacheInvalidationStats
import|;
end_import

begin_class
class|class
name|BackgroundReadStats
block|{
name|CacheInvalidationStats
name|cacheStats
decl_stmt|;
name|long
name|readHead
decl_stmt|;
name|long
name|cacheInvalidationTime
decl_stmt|;
name|long
name|populateDiffCache
decl_stmt|;
name|long
name|lock
decl_stmt|;
name|long
name|dispatchChanges
decl_stmt|;
name|long
name|totalReadTime
decl_stmt|;
name|long
name|numExternalChanges
decl_stmt|;
name|long
name|externalChangesLag
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|cacheStatsMsg
init|=
literal|"NOP"
decl_stmt|;
if|if
condition|(
name|cacheStats
operator|!=
literal|null
condition|)
block|{
name|cacheStatsMsg
operator|=
name|cacheStats
operator|.
name|summaryReport
argument_list|()
expr_stmt|;
block|}
return|return
literal|"ReadStats{"
operator|+
literal|"cacheStats:"
operator|+
name|cacheStatsMsg
operator|+
literal|", head:"
operator|+
name|readHead
operator|+
literal|", cache:"
operator|+
name|cacheInvalidationTime
operator|+
literal|", diff: "
operator|+
name|populateDiffCache
operator|+
literal|", lock:"
operator|+
name|lock
operator|+
literal|", dispatch:"
operator|+
name|dispatchChanges
operator|+
literal|", numExternalChanges:"
operator|+
name|numExternalChanges
operator|+
literal|", externalChangesLag:"
operator|+
name|externalChangesLag
operator|+
literal|", totalReadTime:"
operator|+
name|totalReadTime
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

