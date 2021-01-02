package fima.services.utils

interface ToProtoConvertable<P> {
    fun toProto(): P
}

interface FromProtoConvertable<D> {
    fun fromProto(): D
}

object ProtoUtils {

    fun <D> Collection<FromProtoConvertable<D>>.fromProto() = this.map { it.fromProto() }
    fun <P> Collection<ToProtoConvertable<P>>.toProto() = this.map { it.toProto() }

}