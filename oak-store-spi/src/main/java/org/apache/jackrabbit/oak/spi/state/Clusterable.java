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
name|state
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Interface for bearing cluster node specific information.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Clusterable
block|{
comment|/**      *<p>      * Will return a unique number per instance across the cluster. It will only make its best      * effort to preserve the same number across restarts but it must be unique across the cluster.      *</p>      *       * @return Cannot be null or empty.      */
annotation|@
name|NotNull
name|String
name|getInstanceId
parameter_list|()
function_decl|;
comment|/**      *<p>Returns the visibility token of the underlying NodeStore. A 'visibility      * token' is an opaque String that can be used to verify if changes done on      * one NodeStore are visible on another NodeStore of the same cluster. This      * can be achieved by generating such a visibility token on the source      * NodeStore, passing it on to the target NodeStore (by whatever means) and      * checking for visibility on that target NodeStore.</p>      *      *<p>The visibility check returns true if the target NodeStore sees at least      * all the changes that the source NodeStore saw at time of visibility token      * generation. Once a visibility token is visible on a particular NodeStore      * it will always return true ever after. This also implies that the      * visibility check can only state whether at least all source changes are      * visible on the target and that it is independent of any further      * modifications.</p>      *      *<p>When source and target NodeStore are identical, the visibility check is      * expected to return true, immediately. This is based on the assumption      * that with a session.refresh() on that NodeStore you'll always get the      * latest changes applied by any other session locally.</p>      *      *<p>Visibility tokens are meant to be lightweight and are not expected to be      * persisted by the implementor. Nevertheless they should survive their      * validity in the case of crashes of the source and/or the target instance.</p>      */
annotation|@
name|Nullable
name|String
name|getVisibilityToken
parameter_list|()
function_decl|;
comment|/**      *<p>Checks if the underlying NodeStore sees at least the changes that were      * visible at the time the visibility token was created on potentially      * another instance if in a clustered NodeStore setup.</p>      *      *<p>If the visibility token was created on the underlying NodeStore this      * check always returns true, immediately.</p>      *       * @param visibilityToken      *            the visibility token that was created on another instance in a      *            clustered NodeStore setup. Providing null is not supported and      *            might throw a RuntimeException      * @param maxWaitMillis      *            if&gt;-1 waits (at max this many milliseconds if&gt;0,      *            forever if ==0) until the underlying NodeStore sees at least      *            the changes represented by the provided visibility token. if      *&lt; 0 the method does not wait      * @return true if the underlying NodeStore sees at least the changes that      *         were visible at the time the visibility token was created      * @throws InterruptedException      *             (optionally) thrown if interrupted while waiting      */
name|boolean
name|isVisible
parameter_list|(
annotation|@
name|NotNull
name|String
name|visibilityToken
parameter_list|,
name|long
name|maxWaitMillis
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

