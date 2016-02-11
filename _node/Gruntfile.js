'use strict';

module.exports = function (grunt) {
	grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    concurrent: {
      dev: {
        tasks: ['nodemon']
      }
    },
    nodemon: {
      dev: {
        script: 'bin/www',
        options: {
          args: ['dev'],
          ignore: ['node_modules/**']
        }
      }
    },
    shell: {
      jasmine: {
        command: 'jasmine'
      }
    }
	});

  grunt.loadNpmTasks('grunt-concurrent');
  grunt.loadNpmTasks('grunt-shell');
	grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-nodemon');

  grunt.registerTask('dev', [ 'shell:jasmine', 'nodemon' ]);
  grunt.registerTask('default', [ 'shell:jasmine', 'nodemon' ]);
};