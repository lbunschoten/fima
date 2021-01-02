package fima.api.utils

interface ToProtoConvertable<P> {
    fun toProto(): P
}

interface FromProtoConvertable<P, D> {
    fun fromProto(proto: P): D
}
