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
name|mk
operator|.
name|model
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
name|mk
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Represents an internal identifier, uniquely identifying   * a {@link Node} or a {@link Commit}.  *<p/>  * This implementation aims at minimizing the in-memory footprint  * of an identifier instance. therefore it doesn't cash e.g. the hashCode  * or the string representation.  *<p/>  *<b>Important Note:</b><p/>  * An {@link Id} is considered immutable. The<code>byte[]</code>   * passed to {@link Id#Id(byte[])} must not be reused or modified, the same  * applies for the<code>byte[]</code> returned by {@link Id#getBytes()}.  */
end_comment

begin_class
specifier|public
class|class
name|Id
comment|/* implements Comparable<Id> */
block|{
comment|// the raw bytes making up this identifier
specifier|private
specifier|final
name|byte
index|[]
name|raw
decl_stmt|;
comment|/**      * Creates a new instance based on the passed<code>byte[]</code>.      *<p/>      * The passed<code>byte[]</code> mus not be reused, it's assumed      * to be owned by the new<code>Id</code> instance.      *      * @param raw the byte representation      */
specifier|public
name|Id
parameter_list|(
name|byte
index|[]
name|raw
parameter_list|)
block|{
comment|// don't copy the buffer for efficiency reasons
name|this
operator|.
name|raw
operator|=
name|raw
expr_stmt|;
block|}
comment|/**      * Creates an<code>Id</code> instance from its      * string representation as returned by {@link #toString()}.      *<p/>      * The following condition holds true:      *<pre>      * Id someId = ...;      * assert(Id.fromString(someId.toString()).equals(someId));      *</pre>      *      * @param s a string representation of an<code>Id</code>      * @return an<code>Id</code> instance      */
specifier|public
specifier|static
name|Id
name|fromString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|Id
argument_list|(
name|StringUtils
operator|.
name|convertHexToBytes
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// the hashCode is intentionally not stored
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|raw
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Id
condition|)
block|{
name|Id
name|other
init|=
operator|(
name|Id
operator|)
name|obj
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|raw
argument_list|,
name|other
operator|.
name|raw
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// the string representation is intentionally not stored
return|return
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|raw
argument_list|)
return|;
block|}
comment|//    @Override
comment|//    public int compareTo(Id o) {
comment|//        byte[] other = o.getBytes();
comment|//        int len = Math.min(raw.length, other.length);
comment|//
comment|//        for (int i = 0; i< len; i++) {
comment|//            if (raw[i] != other[i]) {
comment|//                return raw[i] - other[i];
comment|//            }
comment|//        }
comment|//        return raw.length - other.length;
comment|//    }
comment|/**      * Returns the raw byte representation of this identifier.      *<p/>      * The returned<code>byte[]</code><i>MUST NOT</i> be modified!      *      * @return the raw byte representation      */
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
comment|// don't copy the buffer for efficiency reasons
return|return
name|raw
return|;
block|}
block|}
end_class

end_unit

