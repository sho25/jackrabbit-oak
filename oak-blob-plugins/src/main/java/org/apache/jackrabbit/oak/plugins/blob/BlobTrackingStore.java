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
name|plugins
operator|.
name|blob
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
name|blob
operator|.
name|datastore
operator|.
name|BlobTracker
import|;
end_import

begin_comment
comment|/**  * Interface to be implemented by a data store which can support local blob id tracking.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobTrackingStore
extends|extends
name|SharedDataStore
block|{
comment|/**      * Registers a tracker in the data store.      * @param tracker      */
name|void
name|addTracker
parameter_list|(
name|BlobTracker
name|tracker
parameter_list|)
function_decl|;
comment|/**      * Gets the traker registered in the data store.      *      * @return tracker      */
name|BlobTracker
name|getTracker
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

