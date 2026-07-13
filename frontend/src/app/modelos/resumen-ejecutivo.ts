export interface ResumenEjecutivo {

  estadoGeneral: string;

  tendencias: string[];

  observaciones: string[];

  recomendaciones: string[];

  // Se mantiene temporalmente mientras migramos
  conclusiones: string[];

}