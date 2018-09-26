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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * An MBean for text extraction statistics.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|TextExtractionStatsMBean
block|{
comment|/**      * Type of this MBean      */
name|String
name|TYPE
init|=
literal|"TextExtractionStats"
decl_stmt|;
comment|/**      * Check whether pre extracted text provider is configured      * @return {@code true} if configured, {@code false} otherwise      */
name|boolean
name|isPreExtractedTextProviderConfigured
parameter_list|()
function_decl|;
comment|/**      * Check whether pre extracted cache should always be used      * @return {@code true} if PEC should always be used, {@code false} otherwise      */
name|boolean
name|isAlwaysUsePreExtractedCache
parameter_list|()
function_decl|;
comment|/**      * Number of text extraction operations performed      * @return the text extraction count      */
name|int
name|getTextExtractionCount
parameter_list|()
function_decl|;
comment|/**      * Total time taken by text extraction      * @return total time taken      */
name|long
name|getTotalTime
parameter_list|()
function_decl|;
comment|/**      * Pre fetch count      * @return no. of prefetch operations      */
name|int
name|getPreFetchedCount
parameter_list|()
function_decl|;
comment|/**      * Size of extracted size      * @return extracted text size      */
name|String
name|getExtractedTextSize
parameter_list|()
function_decl|;
comment|/**      * Bytes read by text extraction      * @return bytes read      */
name|String
name|getBytesRead
parameter_list|()
function_decl|;
comment|/**      * Count of extractions gone timeout      * @return timeout count      */
name|int
name|getTimeoutCount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

